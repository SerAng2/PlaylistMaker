package com.example.my.search.domain.impl

import com.example.my.common.domain.model.Track
import com.example.my.search.domain.interactor.SearchHistoryInteractor
import com.example.my.search.domain.repository.HistoryRepository

class SearchHistoryInteractorImpl(private val repository: HistoryRepository) :
    SearchHistoryInteractor {

    private val MAX_HISTORY_SIZE = 10

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun addTrack(track: Track) { // добавить в историю
        val history = repository.getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }
        repository.saveHistory(history)
    }

    override fun clearHistory() { // очистить историю repository
        repository.clearHistory()
    }
}