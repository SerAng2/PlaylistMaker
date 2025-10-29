 package com.example.my.domain.usecase

import com.example.my.domain.models.SearchResult
import com.example.my.domain.repository.SearchTrackRepository

class GetSearchTrackUseCase(
    private val searchTrackRepository: SearchTrackRepository) {

    suspend fun performSearch(term: String) : SearchResult {
        return searchTrackRepository.performSearch(term)
    }
}