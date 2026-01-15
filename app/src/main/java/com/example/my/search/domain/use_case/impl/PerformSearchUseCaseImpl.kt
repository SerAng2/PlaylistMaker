package com.example.my.search.domain.use_case.impl

import com.example.my.common.domain.model.Track
import com.example.my.search.domain.repository.PerformSearchRepository
import com.example.my.search.domain.use_case.PerformSearchUseCase
import kotlinx.coroutines.flow.Flow

class PerformSearchUseCaseImpl(private val repository: PerformSearchRepository) :
    PerformSearchUseCase {

      override suspend fun performSearch(term: String): Flow<List<Track>> {
        return repository.performSearch(term)
    }
}