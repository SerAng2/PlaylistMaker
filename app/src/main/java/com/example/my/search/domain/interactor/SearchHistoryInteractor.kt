package com.example.my.search.domain.interactor

import com.example.my.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {

     fun getHistory(): Flow<List<Track>>
     fun addTrack(track: Track)
     fun clearHistory()
}