package com.example.playlistMaker.search.domain.interactorImpl

import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.search.domain.interactor.SearchHistoryInteractor
import com.example.playlistMaker.search.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SearchHistoryInteractorImpl(private val repository: HistoryRepository) :
    SearchHistoryInteractor {

    private val MAX_HISTORY_SIZE = 10

    override fun getHistory(): Flow<List<Track>> {
        return repository.getHistory()
    }

    override suspend fun addTrack(track: Track) { // добавить в историю
            val historyFlow = repository.getHistory()
            val history = historyFlow.first().toMutableList()
            history.removeAll { it.trackId == track.trackId }
            history.add(0, track)
            if (history.size > MAX_HISTORY_SIZE) {
                history.removeAt(history.size - 1)
            }
            repository.saveHistory(history)
        }
    

    override suspend fun clearHistory() { // очистить историю repository
            repository.clearHistory()
        }
    }
