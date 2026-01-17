package com.example.playlistMaker.search.domain.repository

import com.example.playlistMaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PerformSearchRepository {

    suspend fun performSearch(term: String): Flow<List<Track>>

}