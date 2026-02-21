package com.example.playlistMaker.mediaLibrary.presentation.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistMaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.example.playlistMaker.mediaLibrary.domain.model.Playlist
import com.example.playlistMaker.mediaLibrary.presentation.mapper.TrackToViewStateMapper
import com.example.playlistMaker.mediaLibrary.presentation.state.FavoritesState
import com.example.playlistMaker.player.presentation.state.TrackViewState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistTrackViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    val allPlaylists = playlistInteractor.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentPlaylistTracks = MutableStateFlow<List<TrackViewState>>(emptyList())
    val currentPlaylistTracks: StateFlow<List<TrackViewState>> = _currentPlaylistTracks.asStateFlow()


    private val _uiState = MutableStateFlow<PlaylistUiState>(PlaylistUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _totalDuration = MutableStateFlow<String>("0 мин")
    val totalDuration = _totalDuration.asStateFlow()

    private val _uiMessage = MutableSharedFlow<String>()
    val uiMessage = _uiMessage.asSharedFlow()

    private val _shareEvent = MutableSharedFlow<ShareEvent>()
    val shareEvent = _shareEvent.asSharedFlow()

    private val favoritesState = MutableLiveData<FavoritesState>()
    val favoritesObserver get() = favoritesState

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            runCatching { playlistInteractor.deletePlaylist(playlistId) }
                .onSuccess { _uiMessage.emit("Плейлист удалён") }
                .onFailure {
                    Log.e("PlaylistVM", "Ошибка удаления плейлиста", it) // 👈 ДОБАВИТЬ ЭТО!
                    _uiMessage.emit("Ошибка удаления: ${it.message}")
                }
        }
    }

    // Загрузка треков плейлиста
    fun loadPlaylistTracks(playlistId: Long) {
        viewModelScope.launch {
            _currentPlaylistTracks.value = emptyList() // Сбрасываем

            playlistInteractor.getPlaylistTracks(playlistId)
                .collect { tracks ->
                    val viewStates = TrackToViewStateMapper.map(tracks)

                    Log.d("ViewModel", "Loaded tracks: ${tracks.size}")

                    _currentPlaylistTracks.value = viewStates
                    favoritesState.value = if (tracks.isEmpty()) {
                        FavoritesState.Empty
                    } else {
                        FavoritesState.Content(viewStates)
                    }

                    _totalDuration.value = calculateTotalDuration(viewStates)
                }
        }
    }

    private fun calculateTotalDuration(tracks: List<TrackViewState>): String {
        if (tracks.isEmpty()) return "0 мин"

        var totalMillis = 0L

        tracks.forEach { track ->
            val trackTime = when {
                track.trackTime == null -> 0L
                track.trackTime.contains(":") -> parseTimeString(track.trackTime) // Если время в формате "3:45"
                else -> track.trackTime.toLongOrNull() ?: 0L
            }
            totalMillis += trackTime
        }

        val totalMinutes = totalMillis / (1000 * 60)

        return if (totalMinutes > 0) "$totalMinutes мин" else "менее 1 мин"
    }

    private fun parseTimeString(timeString: String): Long {
        return try {
            val parts = timeString.split(":")
            when (parts.size) {
                2 -> { // формат "минуты:секунды"
                    val minutes = parts[0].toLong()
                    val seconds = parts[1].toLong()
                    (minutes * 60 + seconds) * 1000
                }

                3 -> { // формат "часы:минуты:секунды"
                    val hours = parts[0].toLong()
                    val minutes = parts[1].toLong()
                    val seconds = parts[2].toLong()
                    (hours * 3600 + minutes * 60 + seconds) * 1000
                }

                else -> 0L
            }
        } catch (e: Exception) {
            0L
        }
    }

    private fun buildShareText(playlist: Playlist, tracks: List<TrackViewState>): String {
        val builder = StringBuilder()
        builder.append("${playlist.name}\n")

        if (playlist.description?.isNotBlank() == true) {
            builder.append("${playlist.description}\n\n")
        }

        builder.append("Количество треков: ${tracks.size}\n\n")
        builder.append("Список треков:\n")

        tracks.forEachIndexed { index, track ->
            val artist = track.artistName ?: "Неизвестный исполнитель"
            val name = track.trackName ?: "Неизвестное название"
            val duration = track.trackTime ?: "0:00"
            builder.append("${index + 1}. $artist - $name ($duration)\n")
        }

        return builder.toString().trimEnd()
    }

    fun sharePlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getAllPlaylists().first().find { it.id == playlistId }
            val tracks = _currentPlaylistTracks.value ?: emptyList()

            if (tracks.isEmpty() || playlist == null) {
                _shareEvent.emit(ShareEvent.EmptyPlaylist)
                return@launch
            }

            val text = buildShareText(playlist, tracks)
            _shareEvent.emit(ShareEvent.ShareText(text))
        }
    }


    // Метод для удаления трека из плейлиста
    fun removeTrackFromPlaylist(playlistId: Long, track: TrackViewState) {
        Log.d("PLAYLIST_TRACK", "removeTrack(playlist=$playlistId, track=${track.trackId})")
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(playlistId, track.trackId)
        }
    }
}

sealed class PlaylistUiState {
    object Idle : PlaylistUiState()
    object Loading : PlaylistUiState()
    data class Success(val message: String) : PlaylistUiState()
    data class Error(val message: String) : PlaylistUiState()
    data class Info(val message: String) : PlaylistUiState()
}

sealed class ShareEvent {
    object EmptyPlaylist : ShareEvent()
    data class ShareText(val text: String) : ShareEvent()
}
