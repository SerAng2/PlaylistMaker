package com.example.my.domain.repository

import com.example.my.domain.models.Track

interface PerformSearchRepository {

    suspend fun performSearch(term: String): List<Track>
}
