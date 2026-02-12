package com.example.playlistMaker.mediaLibrary.domain.use_case

import com.example.playlistMaker.mediaLibrary.domain.repository.PlaylistRepository

class CreatePlaylistUseCaseImpl(
    private val playlistRepository: PlaylistRepository
) : CreatePlaylistUseCase {
    override suspend operator fun invoke(
        title: String,
        description: String,
        coverPath: String?
    ): Result<Unit> {

        return try {
            playlistRepository.createPlaylist(title, description, coverPath)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
