package com.example.my.search.presentation.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my.R
import com.example.my.databinding.FragmentSearchBinding
import com.example.my.player.presentation.state.TrackViewState
import com.example.my.search.presentation.state.TracksState
import com.example.my.search.presentation.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TrackAdapter
    private val viewModel: SearchViewModel by viewModel()

    // ДОБАВЛЕНО: Инициализируем View Binding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    // ДОБАВЛЕНО: Очищаем binding на destroy, чтобы избежать утечек
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoading()

        adapter = TrackAdapter(emptyList()) { track ->
            // Добавление трека в историю поиска
            viewModel.add(track)

            // Проверка на debounce
            if (viewModel.clickDebounce()) {
                // Создание Bundle для передачи данных в фрагмент

                val bundle = Bundle().apply {
                    putParcelable(TRACK_DATA, track) // Используй putSerializable, если Track не реализует Parcelable
                }
                findNavController().navigate(R.id.actionSearchToPlayer, bundle)
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        setupSearchField()
        setupRetryButton()
        setupObservers()

        val savedSearchText = savedInstanceState?.getString(SEARCH_TEXT_KEY) ?: ""
        binding.searchEditText.setText(savedSearchText)
        updateClearButtonVisibility(savedSearchText.isNotEmpty())
    }

    private fun updateClearButtonVisibility(isVisible: Boolean) {
        binding.clearButton.isVisible = isVisible
    }

    private fun setupRetryButton() {
        binding.retryButton.setOnClickListener {
        }
    }

    private fun setupSearchField() {
        with(binding) {

            searchEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    searchDebounce()
                    val isTextNotEmpty = !s.isNullOrEmpty()
                    updateClearButtonVisibility(isTextNotEmpty)
                    if (!isTextNotEmpty) {
                        showPlaceholderNone()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            binding.clearHistoryButton.setOnClickListener {
                viewModel.clearHistory()
                hideSearchHistory()
            }

            searchEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && searchEditText.text.isEmpty()) {
                    viewModel.displaySearchHistory()
                } else {
                    hideSearchHistory()
                }
            }

            searchEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val query = searchEditText.text.toString().trim()
                    if (query.isNotEmpty()) {
                        viewModel.performSearch(query)
                        hideKeyboard()
                    } else {
                        showPlaceholderNone()
                    }
                    true
                } else {
                    false
                }
            }

            clearButton.setOnClickListener {
                searchEditText.text?.clear()
                hideKeyboard()
                showPlaceholderNone()
                viewModel.displaySearchHistory()
            }
        }
    }

    private fun searchDebounce() {
        val query = binding.searchEditText.text.toString().trim()
        viewModel.searchDebounce(query)
    }

    private fun hideSearchHistory() {
        binding.apply {
            historyHeader.visibility = View.GONE
            recyclerView.isVisible = false
            clearHistoryButton.isVisible = false
        }
    }

    private fun showTracks(tracks: List<TrackViewState>) {
        binding.apply {
            progressBar.isVisible = false
            recyclerView.isVisible = true
            placeholderGroup.isVisible = false
            adapter.updateTracks(tracks)
        }
    }

    private fun showHistory(history: List<TrackViewState>) {
        binding.apply {
            progressBar.isVisible = false
            recyclerView.isVisible = true
            placeholderGroup.isVisible = false
            historyHeader.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
            adapter.updateTracks(history)
        }
    }

    private fun showPlaceholderNoResults(message: Int, image: Int) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            placeholderGroup.visibility = View.VISIBLE
            placeholderImage.setImageResource(R.drawable.ic_no_music)
            placeholderText.text = getString(R.string.no_results_found)
            retryButton.visibility = View.GONE
        }
    }

    private fun showPlaceholderError(message: Int, image: Int) {
        with(binding) {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            placeholderGroup.visibility = View.VISIBLE
            placeholderImage.setImageResource(R.drawable.ic_no_connection)
            placeholderText.text = getString(R.string.server_error_message)
            retryButton.visibility = View.VISIBLE
        }
    }

    private fun showPlaceholderNone() {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            placeholderGroup.visibility = View.GONE
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            placeholderGroup.visibility = View.GONE
            historyHeader.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun hideLoading() {
        binding.progressBar.isVisible = false
        }

    companion object {
        private const val SEARCH_TEXT_KEY = "search_text_key"
        const val TRACK_DATA = "track_data"
    }

    private fun setupObservers() {
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is TracksState.Loading -> showLoading()
                is TracksState.Content -> showTracks(state.tracks)
                is TracksState.Error -> showPlaceholderError(state.message, state.image)
                is TracksState.Empty -> showPlaceholderNoResults(state.message, state.image)
                is TracksState.HideLoading -> hideLoading()
                is TracksState.History -> showHistory(state.tracks)
                is TracksState.PlaceholderNone -> showPlaceholderNone()
                is TracksState.HideSearchHistory -> hideSearchHistory()
            }
        }
    }
}
