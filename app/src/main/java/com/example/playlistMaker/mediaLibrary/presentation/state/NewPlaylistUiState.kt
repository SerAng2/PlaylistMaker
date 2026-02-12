package com.example.playlistMaker.mediaLibrary.presentation.state

data class NewPlaylistUiState(
    val title: String = "",
    val description: String = "",
    val coverPath: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isCreateButtonEnabled: Boolean
        get() = title.isNotBlank()

    val hasChanges: Boolean
        get() = title.isNotBlank() || description.isNotBlank() || coverPath != null
}