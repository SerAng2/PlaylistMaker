package com.example.my.search.presentation.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.my.R
import com.example.my.common.domain.model.Track
import com.example.my.creator.Creator
import com.example.my.player.presentation.state.TrackViewState
import com.example.my.search.presentation.state.TracksState
import kotlinx.coroutines.launch

class SearchViewModel() : ViewModel() {

    private val searchTrackUseCase = Creator.provideGetPerformSearchUseCase()
    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()
    private var isClickAllowed = true
    private var lastSearchTerm: String? = null
    private var handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<TracksState>(TracksState.PlaceholderNone)
    fun observeState(): LiveData<TracksState> = stateLiveData

    init {
        displaySearchHistory()
    }

    private val searchRunnable = Runnable {
        val query = lastSearchTerm
        if (!query.isNullOrEmpty()) {
            searchTracks(query)
        }
    }

    fun searchDebounce(query: String) {
        lastSearchTerm = query
        handler.removeCallbacks(searchRunnable)
        if (query.isNotEmpty()) {
            handler.postDelayed(searchRunnable, CLICK_DEBOUNCE_DELAY)
        } else {
            displaySearchHistory()
        }
    }
    fun performSearch(query: String) {
        if (query.isNotEmpty()) {
            searchTracks(query)
        } else {
            displaySearchHistory()
        }
    }

    fun searchTracks(query: String) {
        lastSearchTerm = query
        stateLiveData.postValue(TracksState.Loading)
        viewModelScope.launch {
            try {
                val tracks = searchTrackUseCase.performSearch(query)
                if (tracks.isEmpty()) {
                    stateLiveData.postValue(
                        TracksState.Empty(
                            R.string.no_results_found,
                            R.drawable.ic_no_music
                        )
                    )
                } else {
                    val uiModels = tracks.map { it.toTrackViewState() } // Новый метод
                    stateLiveData.postValue(TracksState.Content(uiModels))
                }
            } catch (e: Exception) {
                stateLiveData.postValue(
                    TracksState.Error(
                        R.string.server_error_message,
                        R.drawable.ic_no_connection
                    )
                )
            }
        }
    }

    fun displaySearchHistory() {
        val history = searchHistoryInteractor.getHistory() // List<Track>
        if (history.isNotEmpty()) {
            val uiModels = history.map { it.toTrackViewState() } // Преобразование в List<TrackViewState>
            stateLiveData.postValue(TracksState.History( uiModels)) // Теперь типы совпадают — ошибка уйдет
        } else {
            stateLiveData.postValue(TracksState.PlaceholderNone)
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        stateLiveData.postValue(TracksState.PlaceholderNone)
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun add(track: TrackViewState) {
        val domainTrack = track.toDomainTrack()
        searchHistoryInteractor.addTrack(domainTrack)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }

    fun Track.toTrackViewState(): TrackViewState {
        return TrackViewState(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTime = trackTime ?: "Unknown",
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            genre = genre,
            country = country,
            previewUrl = previewUrl
        )
    }

    fun TrackViewState.toDomainTrack(): Track {
        return Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTime = trackTime,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            genre = genre,
            country = country,
            previewUrl = previewUrl
        )
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 2000L
        fun getFactory(): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return SearchViewModel() as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}