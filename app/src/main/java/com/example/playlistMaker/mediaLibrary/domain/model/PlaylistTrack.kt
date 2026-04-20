package com.example.playlistMaker.mediaLibrary.domain.model

import com.example.playlistMaker.common.domain.model.Track
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class PlaylistTrack @OptIn(ExperimentalTime::class) constructor(
    val trackId: String,
    val playlistId: Long,
    val addedAt: Instant,
    val track: Track? = null
)
