package com.example.my.domain.impl

import com.example.my.domain.interactor.SearchHistoryInteractor
import com.example.my.domain.models.Track
import com.example.my.domain.repository.HistoryRepository

class SearchHistoryInteractorImpl(private val repository: HistoryRepository) : SearchHistoryInteractor {

    private val MAX_HISTORY_SIZE = 10

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun addTrack(track: Track) {
        val history = repository.getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }
        repository.saveHistory(history)
    }
}
