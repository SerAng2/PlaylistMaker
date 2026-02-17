package com.example.playlistMaker.mediaLibrary.data.mapper

import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.mediaLibrary.data.db.entity.TrackEntity

class TrackDbConvertor {

    fun mapToData(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTime = track.trackTime,
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite
        )
    }

    fun mapToDomain(track: TrackEntity): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite,
            genre = track.primaryGenreName ?: ""
        )
    }
}
