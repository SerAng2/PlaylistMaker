package com.example.my.player.presentation.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackViewState(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val genre: String?,
    val country: String?,
    val previewUrl: String
) : Parcelable