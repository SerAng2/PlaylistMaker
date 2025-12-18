package com.example.my.mediaLibrary.presentation.view_model

import androidx.lifecycle.ViewModel
import com.example.my.R

class MediaLibraryViewModel() : ViewModel() {

    val tabTitles = listOf(
        R.string.favorite_tracks,
        R.string.playlists
    )
}
