package com.example.my.search.domain.repository

import com.example.my.common.domain.model.Track

interface PerformSearchRepository {

    suspend fun performSearch(term: String): List<Track>
}