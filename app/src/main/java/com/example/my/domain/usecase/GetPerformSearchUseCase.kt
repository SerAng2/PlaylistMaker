package com.example.my.domain.usecase

import com.example.my.domain.models.Track
import com.example.my.domain.repository.PerformSearchRepository

class GetPerformSearchUseCase(private val repository: PerformSearchRepository) {

      suspend fun performSearch(term: String): List<Track> {
        return repository.performSearch(term)
    }
}
