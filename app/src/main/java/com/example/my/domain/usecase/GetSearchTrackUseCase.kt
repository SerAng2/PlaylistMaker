 package com.example.my.domain.usecase

import com.example.my.domain.repository.SearchTrackRepository

class GetSearchTrackUseCase(
    private val searchTrackRepository: SearchTrackRepository) {

    suspend fun performSearch(term: String): Unit {
        searchTrackRepository.performSearch(term)
    }
}