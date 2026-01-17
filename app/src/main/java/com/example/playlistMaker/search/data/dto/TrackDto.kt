package com.example.playlistMaker.search.data.dto

data class TrackDto(
    val trackName: String?,
    val artistName: String?,
    val releaseDate: String?,
    val trackTimeMillis: Long?,
    val collectionId: Int?,
    val artworkUrl100: String,
    val country: String?,
    val trackId: Int?,
    val primaryGenreName: String?,
    val collectionName: String?,
    val previewUrl: String
)