package com.example.my.search.domain.repository

import com.example.my.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PerformSearchRepository {

    suspend fun performSearch(term: String): Flow<List<Track>>

}