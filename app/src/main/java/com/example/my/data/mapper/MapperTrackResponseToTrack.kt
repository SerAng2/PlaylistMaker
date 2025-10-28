package com.example.my.data.mapper

import com.example.my.TimeUtils
import com.example.my.data.dto.TrackDto
import com.example.my.domain.models.Track

object MapperTrackResponseToTrack {
    fun mapTrackResponseToTrack(trackResponse: TrackDto): Track {
        val trackName = trackResponse.trackName ?: "Unknown"
        val artistName = trackResponse.artistName ?: "Unknown"
        val trackTimeMillis = trackResponse.trackTimeMillis ?: 0L
        val artworkUrl = trackResponse.artworkUrl100
        val formattedTime = if (trackTimeMillis > 0) {
            TimeUtils.formatTime(trackTimeMillis.toInt())
        } else {
            ""
        }
        val genre = trackResponse.primaryGenreName ?: "Unknown"
        val country = trackResponse.country ?: "Unknown"
        val releaseDate = trackResponse.releaseDate ?: ""
        val collectionName = trackResponse.collectionName ?: ""
        val previewUrl = trackResponse.previewUrl

        return Track(
            trackId = trackResponse.trackId ?: 0,
            trackName = trackName,
            artistName = artistName,
            trackTime = formattedTime,
            artworkUrl100 = artworkUrl,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = genre,
            genre = genre,
            country = country,
            previewUrl = previewUrl
        )
    }
}