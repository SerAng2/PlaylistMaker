package com.example.playlistMaker.mediaLibrary.presentation.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.example.playlistMaker.mediaLibrary.domain.model.Playlist
import com.example.playlistMaker.mediaLibrary.presentation.mapper.TrackToViewStateMapper
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

    // В ViewModel — ОБЯЗАТЕЛЬНО ИНИЦИАЛИЗИРУЙТЕ ПЕРВОНАЧАЛЬНОЕ ЗНАЧЕНИЕ
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
    private val _currentPlaylist = MutableStateFlow<Playlist?>(null)
    val currentPlaylist: StateFlow<Playlist?> = _currentPlaylist.asStateFlow()

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
            _uiState.value = PlaylistUiState.Loading
            try {
                val playlist = playlistInteractor.getPlaylistById(playlistId).first()

                // ✅ ТЕПЕРЬ ОБНОВЛЯЕМ ТОТ ПОТОК, НА КОТОРЫЙ ПОДПИСАН FRAGMENT
                _currentPlaylist.value = playlist  // ← ВАЖНО!

                val tracks = playlistInteractor.getPlaylistTracks(playlistId)
                val trackViewStates = TrackToViewStateMapper.map(tracks)

                _currentPlaylistTracks.value = trackViewStates
                _totalDuration.value = calculateTotalDuration(trackViewStates)

                _uiState.value = PlaylistUiState.Success("Треки загружены")

            } catch (e: Exception) {
                _uiState.value = PlaylistUiState.Error("Ошибка загрузки: ${e.message}")
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

    private fun buildShareText(): String {
        val playlist = _currentPlaylist.value ?: return "Плейлист не загружен"
        val tracks = _currentPlaylistTracks.value ?: return "Нет треков"

        if (tracks.isEmpty()) return "В данном плейлисте нет треков, которыми можно поделиться."

        val builder = StringBuilder()

        // Название плейлиста
        builder.append("${playlist.name}\n")

        // Описание (если есть)
        if (playlist.description?.isNotBlank() == true) {
            builder.append("${playlist.description}\n\n")
        }

        // Количество треков
        builder.append("Количество треков: ${tracks.size}\n\n")

        // Заголовок списка
        builder.append("Список треков:\n")

        // Перебираем треки
        tracks.forEachIndexed { index, track ->
            val artist = track.artistName ?: "Неизвестный исполнитель"
            val name = track.trackName ?: "Неизвестное название"
            val duration = track.trackTime ?: "0:00"
            builder.append("${index + 1}. $artist - $name ($duration)\n")
        }

        return builder.toString().trimEnd()
    }

    fun sharePlaylist() {
        viewModelScope.launch {
            val shareText = buildShareText()
            Log.d("PlaylistTrackVM", "buildShareText result: ${shareText.take(200)}...")

            when {
                shareText == "Плейлист не загружен" ||
                        shareText == "Нет треков" ||
                        shareText == "В данном плейлисте нет треков, которыми можно поделиться." -> {
                    _shareEvent.emit(ShareEvent.EmptyPlaylist)
                    Log.d("PlaylistTrackVM", "Emitting EmptyPlaylist")
                }
                else -> {
                    _shareEvent.emit(ShareEvent.ShareText(shareText))
                    Log.d("PlaylistTrackVM", "Emitting ShareText: ${shareText.lines().first()}")
                }
            }
        }
    }


    // Метод для удаления трека из плейлиста
    fun removeTrackFromPlaylist(track: TrackViewState, playlistId: Long) =
        viewModelScope.launch {
            // 1. удаляем и СРАЗУ получаем актуальный список
            val actualTracks = playlistInteractor.removeTrackFromPlaylist(playlistId, track.trackId)

            // 2. кладём его в StateFlow → RecyclerView перерисуется
            _currentPlaylistTracks.value = TrackToViewStateMapper.map(actualTracks)

            // 3. пересчитываем длительность
            _totalDuration.value = calculateTotalDuration(_currentPlaylistTracks.value)
        }
}



sealed class PlaylistUiState {
    object Idle : PlaylistUiState()
    object Loading : PlaylistUiState()
    data class Success(val message: String) : PlaylistUiState()
    data class Error(val message: String) : PlaylistUiState()
}

sealed class ShareEvent {
    object EmptyPlaylist : ShareEvent()
    data class ShareText(val text: String) : ShareEvent()
}
