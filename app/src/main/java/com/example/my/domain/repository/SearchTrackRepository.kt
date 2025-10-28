package com.example.my.domain.repository

import com.example.my.domain.models.SearchResult

interface SearchTrackRepository {
    suspend fun performSearch(term: String): SearchResult
}