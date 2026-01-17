package com.example.playlistMaker.player.presentation.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackViewState(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val genre: String?,
    val country: String?,
    val previewUrl: String,
    var isFavorite: Boolean = false
) : Parcelable {
    fun getCoverArtwork(): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }
}
