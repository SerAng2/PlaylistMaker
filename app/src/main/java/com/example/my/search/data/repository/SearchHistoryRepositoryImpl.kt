package com.example.my.search.data.repository

import android.content.SharedPreferences
import com.example.my.common.domain.model.Track
import com.example.my.search.domain.repository.HistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences?,
    private val gson: Gson?
) : HistoryRepository {

    private val HISTORY_KEY = "search_history"

    override fun getHistory(): List<Track> { // получить историю repository
        val json = sharedPreferences?.getString(HISTORY_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson?.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    override fun clearHistory() { // очистить историю repository
        sharedPreferences?.edit()?.remove(HISTORY_KEY)?.apply()
    }

    override fun saveHistory(history: List<Track>) { // сохранить историю repository
        val json = gson?.toJson(history)
        sharedPreferences?.edit()?.putString(HISTORY_KEY, json)?.apply()
    }
}
