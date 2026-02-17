package com.example.playlistMaker.mediaLibrary.domain.use_case

import com.example.playlistMaker.mediaLibrary.domain.repository.PlaylistRepository

class UpdatePlaylistUseCaseImpl(
    private val playlistRepository: PlaylistRepository
) : UpdatePlaylistUseCase {
    override suspend operator fun invoke(
        id: Long,
        name: String,
        description: String?,
        coverPath: String?
    ) = playlistRepository.updatePlaylist(id, name, description, coverPath)
}