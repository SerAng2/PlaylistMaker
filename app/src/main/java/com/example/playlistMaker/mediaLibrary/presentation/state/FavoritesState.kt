package com.example.playlistMaker.mediaLibrary.presentation.state

import com.example.playlistMaker.player.presentation.state.TrackViewState

sealed class FavoritesState {
    object Empty : FavoritesState()
    data class Content(val tracks: List<TrackViewState>) : FavoritesState()
}