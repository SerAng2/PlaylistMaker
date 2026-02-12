package com.example.playlistMaker.mediaLibrary.domain.repository

interface FileManagerRepository {
    suspend fun copyImageToInternalStorage(sourcePath: String, directoryName: String): String?
}