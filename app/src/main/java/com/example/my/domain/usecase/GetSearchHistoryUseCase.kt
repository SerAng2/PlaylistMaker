package com.example.my.domain.usecase

import com.example.my.domain.models.Track
import com.example.my.domain.interactor.SearchHistoryInteractor

class GetSearchHistoryUseCase(
    private val searchHistoryInteractor: SearchHistoryInteractor
) {

    fun getHistory(): List<Track> {
        return searchHistoryInteractor.getHistory()
    }

    fun addTrack(track: Track) {
        searchHistoryInteractor.addTrack(track)
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
    }
}
