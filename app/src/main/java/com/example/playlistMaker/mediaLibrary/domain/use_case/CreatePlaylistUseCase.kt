package com.example.playlistMaker.mediaLibrary.domain.use_case

import android.util.Log
import com.example.playlistMaker.mediaLibrary.domain.repository.PlaylistRepository

class CreatePlaylistUseCase(
    private val playlistRepository: PlaylistRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        coverPath: String?
    ): Result<Unit> {
        Log.d("PlaylistCreation", "UseCase invoked: name=$title, description=$description")
        return try {
            playlistRepository.createPlaylist(title, description, coverPath)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}