package com.example.my.search.domain.impl

import com.example.my.common.domain.model.Track
import com.example.my.search.domain.interactor.SearchHistoryInteractor
import com.example.my.search.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SearchHistoryInteractorImpl(private val repository: HistoryRepository) :
    SearchHistoryInteractor {

    private val MAX_HISTORY_SIZE = 10

    override fun getHistory(): Flow<List<Track>> {
        return repository.getHistory()
    }

    override fun addTrack(track: Track) { // добавить в историю
        runBlocking {
            val historyFlow = repository.getHistory()
            val history = historyFlow.first().toMutableList()
            history.removeAll { it.trackId == track.trackId }
            history.add(0, track)
            if (history.size > MAX_HISTORY_SIZE) {
                history.removeAt(history.size - 1)
            }
            repository.saveHistory(history)
        }
    }

    override fun clearHistory() {
        runBlocking {// очистить историю repository
            repository.clearHistory()
        }
    }
}