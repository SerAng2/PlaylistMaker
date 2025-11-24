package com.example.my.domain.repository

import com.example.my.domain.models.Track

interface HistoryRepository {

    fun getHistory(): List<Track>
    fun saveHistory(history: List<Track>)
    fun clearHistory()
}