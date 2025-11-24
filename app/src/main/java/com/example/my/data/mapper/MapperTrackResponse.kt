package com.example.my.data.mapper

import com.example.my.presentation.utils.TimeUtils
import com.example.my.data.dto.TrackDto
import com.example.my.domain.models.Track

object MapperTrackResponse {
    fun mapTrackResponse(trackDto: TrackDto): Track {
        val trackName = trackDto.trackName ?: "Unknown"
        val artistName = trackDto.artistName ?: "Unknown"
        val trackTimeMillis = trackDto.trackTimeMillis ?: 0L
        val artworkUrl = trackDto.artworkUrl100
        val formattedTime = if (trackTimeMillis > 0) {
            TimeUtils.formatTime(trackTimeMillis.toInt())
        } else {
            ""
        }
        val genre = trackDto.primaryGenreName ?: "Unknown"
        val country = trackDto.country ?: "Unknown"
        val releaseDate = trackDto.releaseDate ?: ""
        val collectionName = trackDto.collectionName ?: ""
        val previewUrl = trackDto.previewUrl

        return Track(
            trackId = trackDto.trackId ?: 0,
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
