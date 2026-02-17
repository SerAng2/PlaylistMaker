package com.example.playlistMaker.mediaLibrary.domain.use_case

interface UpdatePlaylistUseCase {
    suspend operator fun invoke(
        id: Long,
        name: String,
        description: String?,
        coverPath: String?
    )
}
