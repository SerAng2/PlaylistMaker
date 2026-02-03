package com.example.playlistMaker.search.data.repositoryImpl

import android.content.SharedPreferences
import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.search.domain.repository.HistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences?,
    private val gson: Gson?,
) : HistoryRepository {

    override fun getHistory(): Flow<List<Track>> { // получить историю repository
        return flow {
            val json = sharedPreferences?.getString(HISTORY_KEY, null) ?: ""

            if (json.isNotEmpty()) {
                val type = object : TypeToken<List<Track>>() {}.type
                val historyList: List<Track> = gson?.fromJson(json, type) ?: emptyList()
                emit(historyList)
            } else {
                emit(emptyList())
            }
        }
    }

    override suspend fun clearHistory() { // очистить историю repository
        withContext(Dispatchers.IO) {
            sharedPreferences?.edit()?.remove(HISTORY_KEY)?.apply()
        }
    }

    override suspend fun saveHistory(history: List<Track>) { // сохранить историю repository
        val json = gson?.toJson(history)
        sharedPreferences?.edit()?.putString(HISTORY_KEY, json)?.apply()
    }

    companion object {
        private const val HISTORY_KEY = "search_history"
    }
}
