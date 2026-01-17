package com.example.playlistMaker.search.presentation.state

import com.example.playlistMaker.player.presentation.state.TrackViewState

sealed interface TracksState {

    object Loading : TracksState

    data class Content(
        val tracks: List<TrackViewState>
    ) : TracksState
    data class Error(
        val message: Int,
        val image: Int
    ) : TracksState

    data class Empty(
        val message: Int,
        val image: Int
    ) : TracksState

   data class History (
       val tracks: List<TrackViewState>
    ) : TracksState

    object HideLoading : TracksState

    object PlaceholderNone : TracksState

    object HideSearchHistory : TracksState
}