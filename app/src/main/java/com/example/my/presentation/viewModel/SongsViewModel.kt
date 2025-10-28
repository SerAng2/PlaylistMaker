package com.example.my.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.my.data.mapper.MapperTrackResponseToTrack.mapTrackResponseToTrack
import com.example.my.domain.usecase.SearchSongsUseCase


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongsViewModel(
    private val searchSongsUseCase: SearchSongsUseCase
) : ViewModel() {

    private val _uiState =
        MutableLiveData<SearchUiState>()
    val uiState: LiveData<SearchUiState> get() = _uiState

    private var lastSearchTerm = ""
    private var searchJob: Job? = null

    fun performSearch(term: String) {
        if (term == lastSearchTerm) return
        lastSearchTerm = term
        searchJob?.cancel()

        _uiState.value = SearchUiState.Loading

        searchJob = viewModelScope.launch {
            try {
                val tracks = withContext(Dispatchers.IO) {
                    searchSongsUseCase(term)
                }
                if (tracks.isEmpty()) {
                    _uiState.value = SearchUiState.NoResults
                } else {
                    _uiState.value = SearchUiState.Success(tracks)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SearchUiState.Error
            }
        }
    }
}
private fun performSearch(term: String) { // выполнить поиск
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

