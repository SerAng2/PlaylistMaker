package com.example.my.domain.usecase

import com.example.my.domain.models.Track
import com.example.my.domain.repository.SearchHistoryRepository

class GetSearchHistoryUseCase(
    private val searchHistoryRepository: SearchHistoryRepository
) {

    fun getHistory(): List<Track> {
        return searchHistoryRepository.getHistory()
    }

    fun addTrack(track: Track) {
        searchHistoryRepository.addTrack(track)
    }

    fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }
}
