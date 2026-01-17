package com.example.playlistMaker.mediaLibrary.data.mapper

import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.mediaLibrary.data.db.entity.TrackEntity

class TrackDbConvertor {
    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.artworkUrl100,
            track.trackName,
            track.artistName,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.trackTime,
            track.previewUrl,
            isFavorite = track.isFavorite
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }
}
