package com.example.playlistMaker.player.presentation.mapper

import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.player.presentation.state.TrackViewState

fun Track.toTrackViewState(): TrackViewState {
    return TrackViewState(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        artworkUrl100 = artworkUrl100,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        genre = genre,
        country = country,
        previewUrl = previewUrl
    )
}


fun TrackViewState.toDomainTrack(): Track {
    return Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        artworkUrl100 = artworkUrl100,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        genre = genre,
        country = country,
        previewUrl = previewUrl,
        isFavorite = isFavorite
    )
}