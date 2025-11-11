package com.example.my.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my.R
import com.example.my.databinding.ActivitySearchBinding
import com.example.my.domain.models.Track
import com.example.my.presentation.Creator.provideGetPerformSearchUseCase
import com.example.my.presentation.Creator.provideSearchHistoryInteractor
import com.example.my.presentation.Creator.provideSearchHistoryInteractorImpl
import com.example.my.presentation.TrackAdapter
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: TrackAdapter
    private var lastSearchTerm: String? = null
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading()

        adapter = TrackAdapter(emptyList()) { track ->
            // Добавление трека в историю поиска
            provideSearchHistoryInteractorImpl().addTrack(track)

            // Создание Intent для перехода на экран Player
            if (clickDebounce()) {
                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra(TRACK_DATA, track) // Передаем объект Track
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
        displaySearchHistory()

        val savedSearchText = savedInstanceState?.getString(SEARCH_TEXT_KEY) ?: ""
        binding.searchEditText.setText(savedSearchText)
        updateClearButtonVisibility(savedSearchText.isNotEmpty())
    }

    private val searchRunnable = Runnable {
        val query = binding.searchEditText.text.toString().trim()
        if (query.isNotEmpty()) {
            performSearchCall(query)
        } else {
            showPlaceholderNone()
            displaySearchHistory()
        }
    }
    private fun performSearchCall(query: String) {
        lifecycleScope.launch {  // Запуск корутины
            showLoading()  // Показ загрузки
            try {
                val tracks = provideGetPerformSearchUseCase().performSearch(query)  // suspend-вызов Repo
                if (tracks.isEmpty()) {
                    showPlaceholderNoResults()
                } else {
                    showTracks(tracks)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showPlaceholderError()
            } finally {
                hideLoading() // Скрыть загрузку
            }
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, CLICK_DEBOUNCE_DELAY)
    }

    private fun updateClearButtonVisibility(isVisible: Boolean) {
        binding.clearButton.isVisible = isVisible
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRetryButton() { // кнопка обновить
        binding.retryButton.setOnClickListener {
            lastSearchTerm?.let {
                performSearchCall(it)
            }
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
                        displaySearchHistory()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            binding.clearHistoryButton.setOnClickListener {
                provideSearchHistoryInteractor().clearHistory()
                hideSearchHistory()
            }

            searchEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && searchEditText.text.isEmpty()) {
                    displaySearchHistory()
                } else {
                    hideSearchHistory()
                }
            }

            searchEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val query = searchEditText.text.toString().trim()
                    if (query.isNotEmpty()) {
                        performSearchCall(query)
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
                displaySearchHistory()
            }
        }
    }

    private fun displaySearchHistory() {
        val history =  provideSearchHistoryInteractor().getHistory()
        if (history.isNotEmpty()) {
            binding.historyHeader.visibility = View.VISIBLE
            binding.recyclerView.isVisible = true
            binding.clearHistoryButton.isVisible = true
            adapter.updateTracks(history)
        } else {
            hideSearchHistory()
        }
    }

    private fun hideSearchHistory() {
        binding.historyHeader.visibility = View.GONE
        binding.recyclerView.isVisible = false
        binding.clearHistoryButton.isVisible = false
    }

    private fun showTracks(tracks: List<Track>) {
        binding.apply {
            progressBar.isVisible = false
            recyclerView.isVisible = true
            placeholderGroup.isVisible = false
            adapter.updateTracks(tracks)
        }
    }

    private fun showPlaceholderNoResults() {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            placeholderGroup.visibility = View.VISIBLE
            placeholderImage.setImageResource(R.drawable.ic_no_music)
            placeholderText.text = getString(R.string.no_results_found)
            retryButton.visibility = View.GONE
        }
    }

    private fun showPlaceholderError() {
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, binding.searchEditText.text.toString())
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
    private fun hideLoading() {
        binding.apply {
                progressBar.isVisible = false
        }
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "search_text_key"
        const val TRACK_DATA = "track_data"
        private const val CLICK_DEBOUNCE_DELAY = 2000L
    }
}
