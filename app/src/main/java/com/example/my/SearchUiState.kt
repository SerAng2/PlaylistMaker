package com.example.my

sealed class SearchUiState {
    object Loading : SearchUiState()
    data class Success(val tracks: String) : SearchUiState()
    object NoResults : SearchUiState()
    object Error : SearchUiState()
}