package com.example.my.search.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my.R
import com.example.my.databinding.ActivitySearchBinding
import com.example.my.player.presentation.state.TrackViewState
import com.example.my.player.presentation.ui.PlayerActivity
import com.example.my.search.presentation.state.TracksState
import com.example.my.search.presentation.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: TrackAdapter
    private val viewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading()

        adapter = TrackAdapter(emptyList()) { track ->
            // Добавление трека в историю поиска
            viewModel.add(track)

            // Создание Intent для перехода на экран Player
            if (viewModel.clickDebounce()) {
                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra(TRACK_DATA, track)
                }
                // Запуск нового Activity
                startActivity(intent)
            }
        }


        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        setupSearchField()
        setupToolbar()
        setupRetryButton()
        setupObservers()

        val savedSearchText = savedInstanceState?.getString(SEARCH_TEXT_KEY) ?: ""
        binding.searchEditText.setText(savedSearchText)
        updateClearButtonVisibility(savedSearchText.isNotEmpty())
    }

    private fun updateClearButtonVisibility(isVisible: Boolean) {
        binding.clearButton.isVisible = isVisible
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
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
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
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
        viewModel.observeState().observe(this) { state ->
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
