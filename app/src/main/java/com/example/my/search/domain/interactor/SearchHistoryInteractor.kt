package com.example.my.search.domain.interactor

import com.example.my.common.domain.model.Track

interface SearchHistoryInteractor {

     fun getHistory(): List<Track>
     fun addTrack(track: Track)
     fun clearHistory()
}