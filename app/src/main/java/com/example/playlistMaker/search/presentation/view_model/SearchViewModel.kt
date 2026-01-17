package com.example.playlistMaker.search.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistMaker.R
import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.player.presentation.state.TrackViewState
import com.example.playlistMaker.search.domain.interactor.SearchHistoryInteractor
import com.example.playlistMaker.search.domain.use_case.PerformSearchUseCase
import com.example.playlistMaker.search.presentation.state.TracksState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val performSearch: PerformSearchUseCase,
    private val historyInteractor: SearchHistoryInteractor,
) : ViewModel() {

    private var isClickAllowed = true
    private var lastSearchTerm: String? = null
    private var searchJob: Job? = null

    private val stateLiveData = MutableLiveData<TracksState>(TracksState.PlaceholderNone)
    fun observeState(): LiveData<TracksState> = stateLiveData

    fun searchDebounce(changedText: String) {
        if (lastSearchTerm == changedText) {
            return
        }
        lastSearchTerm = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(CLICK_DEBOUNCE_DELAY)
            performSearch(changedText)
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
                performSearch.performSearch(query)
                    .collect { tracks ->
                        if (tracks.isEmpty()) {
                            stateLiveData.postValue(
                                TracksState.Empty(
                                    R.string.no_results_found,
                                    R.drawable.ic_no_music
                                )
                            )
                        } else {
                            val uiModels = tracks.map { it.toTrackViewState() }
                            stateLiveData.postValue(TracksState.Content(uiModels))
                        }
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
        viewModelScope.launch {
            historyInteractor.getHistory()
                .collect { history ->
                    if (history.isNotEmpty()) {
                        val uiModels =
                            history.map { it.toTrackViewState() } // Преобразование в List<TrackViewState>
                        stateLiveData.postValue(TracksState.History(uiModels)) // Теперь типы совпадают — ошибка уйдет
                    } else {
                        stateLiveData.postValue(TracksState.PlaceholderNone)
                    }
                }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyInteractor.clearHistory()
            stateLiveData.postValue(TracksState.PlaceholderNone)
        }
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    fun add(track: TrackViewState) {
        viewModelScope.launch {
            val domainTrack = track.toDomainTrack()
            historyInteractor.addTrack(domainTrack)
        }
    }

    fun Track.toTrackViewState(): TrackViewState {
        return TrackViewState(
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
    }
}