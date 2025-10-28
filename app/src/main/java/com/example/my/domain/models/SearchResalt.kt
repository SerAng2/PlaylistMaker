package com.example.my.domain.models

sealed class SearchResult {
    data class Success(val tracks: List<Track>) : SearchResult()
    object Empty : SearchResult()
    object Error : SearchResult()
    object Loading : SearchResult()
}