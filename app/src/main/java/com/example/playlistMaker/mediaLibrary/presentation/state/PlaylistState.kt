package com.example.playlistMaker.mediaLibrary.presentation.state

sealed interface PlaylistState {

    object Empty : PlaylistState

    data class Content(
        val playlists: List<PlaylistViewState>
    ) : PlaylistState
}