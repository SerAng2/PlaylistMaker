package com.example.playlistMaker.mediaLibrary.presentation.state

sealed class PlaylistState {
    object Empty : PlaylistState()
    data class Content(val playlists: List<PlaylistViewState>) : PlaylistState()
}