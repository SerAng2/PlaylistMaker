package com.example.playlistMaker.player.presentation.view_model

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.mediaLibrary.domain.interactor.FavoriteTrackInteractor
import com.example.playlistMaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.example.playlistMaker.mediaLibrary.domain.model.Playlist
import com.example.playlistMaker.player.presentation.mapper.toDomainTrack
import com.example.playlistMaker.player.presentation.state.PlayerState
import com.example.playlistMaker.player.presentation.state.TrackViewState
import com.example.playlistMaker.player.presentation.utils.formatToMMSS
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val mediaPlayer: MediaPlayer,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val trackStateLiveData = MutableLiveData<TrackViewState?>()
    val observeTrack: LiveData<TrackViewState?> get() = trackStateLiveData

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _addTrackStatus = MutableLiveData<String>()
    val addTrackStatus: LiveData<String> get() = _addTrackStatus

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private var track: Track? = null
    private var timerJob: Job? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    init {
        loadPlaylists()
    }

    fun onPause() {
        pausePlayer()
    }

    fun onPlayButtonClicked() {
        when (playerState.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }
            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }
            else -> {}
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun preparePlayer() {
        val previewUrl = track?.previewUrl
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState.postValue(PlayerState.Prepared())
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerState.postValue(PlayerState.Prepared())
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerState.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(
                    timeMillis = DELAY_TIME_MS
                )
                playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return dateFormat.format(mediaPlayer.currentPosition)
            ?: 0L.formatToMMSS()
    }

    fun loadTrackInfo(track: TrackViewState?) {
        track?.let {
            this.track = track?.toTrack()
            trackStateLiveData.value = track
            viewModelScope.launch {
                val isFav = favoriteTrackInteractor.isTrackFavorite(track.trackId)
                _isFavorite.value = isFav
                trackStateLiveData.value = track.copy(isFavorite = isFav)
                preparePlayer()
            }
        }
    }

    fun onFavoriteClicked(track: TrackViewState) {
        viewModelScope.launch {
            val newIsFavorite = !track.isFavorite
            val updatedTrack = track.copy(isFavorite = newIsFavorite)
            if (newIsFavorite) {
                favoriteTrackInteractor.addTrackToFavorites(updatedTrack.toDomainTrack())
                Log.d("FavoriteRepository", "Saving track: ${track.trackId}, isFavorite: ${track.isFavorite}")
            } else {
                favoriteTrackInteractor.removeTrackFromFavorites(updatedTrack.toDomainTrack())
            }
            _isFavorite.value = newIsFavorite
            trackStateLiveData.value = updatedTrack
        }
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            try {
                val list = playlistInteractor.getAllPlaylists().first()
                _playlists.value = list
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Error loading playlists", e)
            }
        }
    }

    fun addTrackToPlaylist(playlistId: Long, trackId: Long?) {
        viewModelScope.launch {
            try {
                val exists = playlistInteractor.isTrackInPlaylist(playlistId, trackId)
                if (exists) {
                    _addTrackStatus.value = "Трек уже в плейлисте"
                } else {
                    // ✅ ВАЖНО: Добавляем трек
                    playlistInteractor.addTrackToPlaylist(playlistId, trackId)

                    _addTrackStatus.value = "Трек добавлен в плейлист"
                    Log.e("PlaylistInteractor", "Saving track: ${track?.trackId}, playlist: ${playlistId}")
                    loadPlaylists()
                    playlistInteractor.refreshPlaylists()
                 }
            } catch (e: Exception) {
                _addTrackStatus.value = "Ошибка: трек не найден"
            }
        }
    }

    fun TrackViewState.toTrack(): Track {
        return Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTime = trackTime,
            artworkUrl100 = artworkUrl100,
            collectionName = collectionName,
            releaseDate = releaseDate,
            primaryGenreName = primaryGenreName,
            genre = genre ?: "",
            country = country,
            previewUrl = previewUrl,
        )
    }

    companion object {
        private const val DELAY_TIME_MS = 300L
    }
}
