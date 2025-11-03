package com.example.my.domain.repository

import com.example.my.domain.models.Track

interface SearchHistoryRepository {
     fun getHistory(): List<Track>
     fun addTrack(track: Track)
     fun clearHistory()
}
