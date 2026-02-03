package com.example.playlistMaker.search.data.mapper

import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.search.data.dto.TrackDto

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
            trackId = (trackDto.trackId ?: 0).toLong(),
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

    object TimeUtils {
        fun formatTime(millis: Int): String {
            val totalSeconds = millis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }
}