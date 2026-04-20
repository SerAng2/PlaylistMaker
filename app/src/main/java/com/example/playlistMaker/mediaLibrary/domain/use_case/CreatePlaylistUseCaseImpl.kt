package com.example.playlistMaker.mediaLibrary.domain.use_case

import com.example.playlistMaker.mediaLibrary.domain.repository.PlaylistRepository

class CreatePlaylistUseCaseImpl(
    private val playlistRepository: PlaylistRepository
) : CreatePlaylistUseCase {
    override suspend operator fun invoke(
        title: String,
        description: String,
        coverPath: String?
    ): Result<Long> {

        if (title.trim().isEmpty()) {
            return Result.failure(IllegalArgumentException("Название плейлиста не может быть пустым"))
        }

        return try {
            val playlistId = playlistRepository.createPlaylist(
                name = title.trim(),
                description = description.trim(),
                coverPath = coverPath
            )
            Result.success(playlistId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
