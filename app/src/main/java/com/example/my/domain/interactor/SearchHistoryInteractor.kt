package com.example.my.domain.interactor

import com.example.my.domain.models.Track

interface SearchHistoryInteractor {

     fun getHistory(): List<Track>
     fun addTrack(track: Track)
}
