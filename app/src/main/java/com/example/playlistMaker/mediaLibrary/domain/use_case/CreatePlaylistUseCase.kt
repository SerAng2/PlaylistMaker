package com.example.playlistMaker.mediaLibrary.domain.use_case

interface CreatePlaylistUseCase {
    suspend operator fun invoke(
        title: String,
        description: String,
        coverPath: String?
    ): Result<Unit>
}