package com.example.playlistMaker.mediaLibrary.presentation.view_model

import androidx.lifecycle.ViewModel
import com.example.playlistMaker.R

class MediaLibraryViewModel() : ViewModel() {

    val tabTitles = listOf(
        R.string.favorite_tracks,
        R.string.playlists
    )
}
