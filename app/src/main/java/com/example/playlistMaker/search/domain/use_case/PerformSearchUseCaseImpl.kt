package com.example.playlistMaker.search.domain.use_case

import android.util.Log
import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.search.domain.repository.PerformSearchRepository
import kotlinx.coroutines.flow.Flow

class PerformSearchUseCaseImpl(private val repository: PerformSearchRepository) :
    PerformSearchUseCase {

      override suspend fun performSearch(term: String): Flow<List<Track>> {
        return repository.performSearch(term)
    }

    init {
        try {
            Log.e("PerformSearchUseCase", "3")
            // Какой-то код, который может вызвать ошибку
        } catch (e: Exception) {
            Log.e("PerformSearchUseCase", "Ошибка в PerformSearchUseCase", e)
        }
    }
}