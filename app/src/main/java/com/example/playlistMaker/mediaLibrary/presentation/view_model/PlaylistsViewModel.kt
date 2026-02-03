package com.example.playlistMaker.mediaLibrary.presentation.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistMaker.mediaLibrary.domain.interactor.PlaylistInteractor
import com.example.playlistMaker.mediaLibrary.presentation.state.PlaylistState
import com.example.playlistMaker.mediaLibrary.presentation.state.PlaylistViewState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlistState = MutableLiveData<PlaylistState>()
    val playlistObserver: LiveData<PlaylistState> get() = _playlistState

    private val _selectedTrackId = MutableLiveData<Long?>()
    val selectedTrackId: LiveData<Long?> get() = _selectedTrackId

    fun selectTrack(trackId: Long) {
        _selectedTrackId.value = trackId
    }


    fun loadPlaylist() {
        viewModelScope.launch {
            try {
                playlistInteractor.getAllPlaylists().collect { playlists ->
                    Log.d("PlaylistsVM", "Received ${playlists.size} playlists from Flow")
                    playlists.forEach { playlist ->
                        Log.d(
                            "PlaylistsVM",
                            "Playlist: ${playlist.name}, trackCount: ${playlist.trackCount}"
                        )
                    }

                    if (playlists.isNotEmpty()) {
                        val viewStates = playlists.map { playlist ->
                            PlaylistViewState(
                                id = playlist.id,
                                name = playlist.name,
                                description = playlist.description,
                                coverPath = playlist.coverPath,
                                trackCount = playlist.trackCount,
                                trackIds = playlist.trackIds
                            )
                        }
                        _playlistState.value = PlaylistState.Content(viewStates)
                    } else {
                        _playlistState.value = PlaylistState.Empty
                    }
                }
            } catch (e: Exception) {
                _playlistState.value = PlaylistState.Empty
                Log.e("PlaylistsViewModel", "Error loading playlists", e)
            }
        }
    }
}
