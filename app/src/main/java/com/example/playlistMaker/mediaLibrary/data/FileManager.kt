package com.example.playlistMaker.mediaLibrary.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileManager(
    private val context: Context
) {

    suspend fun copyImageToInternalStorage(sourcePath: String, directoryName: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val sourceFile = File(sourcePath)
                if (!sourceFile.exists()) return@withContext null

                val internalDir = File(context.filesDir, directoryName)
                if (!internalDir.exists()) {
                    internalDir.mkdirs()
                }

                val fileName = "${System.currentTimeMillis()}_${sourceFile.name}"
                val destinationFile = File(internalDir, fileName)

                sourceFile.inputStream().use { input ->
                    destinationFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                destinationFile.absolutePath
            } catch (e: Exception) {
                null
            }
        }
    }
}
