package com.example.playlistMaker.mediaLibrary.domain.model

import androidx.media3.extractor.mp4.Track

data class PlaylistWithTracks(
    val playlist: Playlist,
    val tracks: List<Track>
)