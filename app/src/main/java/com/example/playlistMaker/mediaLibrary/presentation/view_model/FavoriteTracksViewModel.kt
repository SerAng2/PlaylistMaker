package com.example.playlistMaker.mediaLibrary.presentation.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistMaker.mediaLibrary.domain.interactor.FavoriteTrackInteractor
import com.example.playlistMaker.mediaLibrary.presentation.state.FavoritesState
import com.example.playlistMaker.player.presentation.mapper.toTrackViewState
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {

    private val favoritesState = MutableLiveData<FavoritesState>()
    val favoritesObserver get() = favoritesState

    init {
        loadFavoriteTracks()
    }

    private fun loadFavoriteTracks() {
        viewModelScope.launch {
            favoriteTrackInteractor.getFavoriteTracks().collect { tracks ->
                Log.d("FavoriteTracksVM", "Loaded ${tracks.size} favorite tracks")
                favoritesState.value = if (tracks.isEmpty()) {
                    FavoritesState.Empty
                } else {
                    FavoritesState.Content(tracks.map { it.toTrackViewState() })
                }
            }
        }
    }
}
