package com.example.my.search.domain.use_case

import com.example.my.common.domain.model.Track
import com.example.my.search.domain.repository.PerformSearchRepository

class GetPerformSearchUseCase(private val repository: PerformSearchRepository) {

      suspend fun performSearch(term: String): List<Track> {
        return repository.performSearch(term)
    }
}
