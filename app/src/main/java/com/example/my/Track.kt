package com.example.my

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
)

data class SearchResponse(
    val resultCount: Int?,
    val results: List<TrackResponse>?,
)

data class TrackResponse(
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long?,
    val artworkUrl100: String
)

