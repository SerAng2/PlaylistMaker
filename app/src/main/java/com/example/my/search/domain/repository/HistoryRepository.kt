package com.example.my.search.domain.repository

import com.example.my.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    fun getHistory(): Flow<List<Track>>
    suspend fun saveHistory(history: List<Track>)
    suspend fun clearHistory()
}