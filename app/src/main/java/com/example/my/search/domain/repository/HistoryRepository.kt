package com.example.my.search.domain.repository

import com.example.my.common.domain.model.Track

interface HistoryRepository {

    fun getHistory(): List<Track>
    fun saveHistory(history: List<Track>)
    fun clearHistory()
}