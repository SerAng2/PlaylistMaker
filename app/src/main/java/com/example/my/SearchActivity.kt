package com.example.my

import Track
import TrackResponse
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my.databinding.ActivitySearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory
    private var lastSearchTerm: String? = null
    private var searchJob: Job? = null

    companion object {
        private const val SEARCH_TEXT_KEY = "search_text_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TrackAdapter(emptyList()) { track ->
            // Добавление трека в историю поиска
            searchHistory.addTrack(track)

            // Создание Intent для перехода на экран Player
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("TRACK_DATA", track) // Передаем объект Track
            }
            // Запуск нового Activity
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        searchHistory = SearchHistory(getSharedPreferences("app_prefs", MODE_PRIVATE))

        setupSearchField()
        setupToolbar()
        setupRetryButton()
        displaySearchHistory()

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
            lastSearchTerm?.let {
                performSearch(it)
            }
        }
    }

    private fun setupSearchField() {
        with(binding) {
            searchEditText.doOnTextChanged { text, _, _, _ ->
                val isTextNotEmpty = !text.isNullOrEmpty()
                updateClearButtonVisibility(isTextNotEmpty)
                if (!isTextNotEmpty) {
                    showPlaceholderNone()
                    displaySearchHistory()
                }
                binding.clearHistoryButton.setOnClickListener {
                    searchHistory.clearHistory()
                    hideSearchHistory()
                }
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
                        performSearch(query)
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
        val history = searchHistory.getHistory()
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

    private fun performSearch(term: String) {
        lastSearchTerm = term
        searchJob?.cancel()
        showLoading()

        searchJob = lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.searchSongs(term)
                }
                if (response.isSuccessful) {
                    val body = response.body()
                    val results = body?.results ?: emptyList()

                    if (results.isEmpty()) {
                        showPlaceholderNoResults()
                    } else {
                        val tracks = results.map { mapTrackResponseToTrack(it) }
                        showTracks(tracks)
                    }
                } else {
                    showPlaceholderError()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showPlaceholderError()
                }
            }
        }
    }

    private fun mapTrackResponseToTrack(trackResponse: TrackResponse): Track {
        val trackName = trackResponse.trackName ?: "Unknown"
        val artistName = trackResponse.artistName ?: "Unknown"
        val trackTimeMillis = trackResponse.trackTimeMillis ?: 0L
        val artworkUrl = trackResponse.artworkUrl100
        val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)

        return Track(
            trackName = trackName,
            artistName = artistName,
            trackTime = formattedTime,
            artworkUrl100 = artworkUrl,
            trackId = trackResponse.trackId ?: 0,
        )
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

    object RetrofitInstance {
        private const val BASE_URL = "https://itunes.apple.com"

        val api: ITunesApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ITunesApi::class.java)
        }
    }
}
