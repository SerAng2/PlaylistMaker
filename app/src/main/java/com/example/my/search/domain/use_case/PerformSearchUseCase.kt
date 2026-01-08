package com.example.my.search.domain.use_case

import com.example.my.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PerformSearchUseCase {

    suspend fun performSearch(term: String): Flow<List<Track>>
}