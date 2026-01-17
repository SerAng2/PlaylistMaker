package com.example.playlistMaker.search.domain.interactor

import com.example.playlistMaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {

    fun getHistory(): Flow<List<Track>>
    suspend fun addTrack(track: Track)
    suspend fun clearHistory()
}