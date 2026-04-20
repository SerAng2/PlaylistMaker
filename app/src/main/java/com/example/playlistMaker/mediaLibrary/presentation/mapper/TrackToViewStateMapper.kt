package com.example.playlistMaker.mediaLibrary.presentation.mapper

import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.player.presentation.state.TrackViewState

object TrackToViewStateMapper {

    fun map(track: Track): TrackViewState {
        return TrackViewState(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            genre = track.genre,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite
        )
    }

    fun map(tracks: List<Track>): List<TrackViewState> {
        return tracks.map { map(it) }
    }
}