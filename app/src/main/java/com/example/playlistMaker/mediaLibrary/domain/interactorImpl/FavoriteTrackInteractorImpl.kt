package com.example.playlistMaker.mediaLibrary.domain.interactorImpl

import android.util.Log
import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.mediaLibrary.domain.interactor.FavoriteTrackInteractor
import com.example.playlistMaker.mediaLibrary.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow

class FavoriteTrackInteractorImpl(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : FavoriteTrackInteractor {

    init {
        Log.d("FavoriteInteractor", "Instance created: ${this.hashCode()}")
    }

    override suspend fun addTrackToFavorites(track: Track) {
        Log.d("FavoriteInteractor", "Adding: ${track.trackName}")
        favoriteTracksRepository.addTrackFavorite(track)
    }
    override suspend fun removeTrackFromFavorites(track: Track) {
        favoriteTracksRepository.removeTrackFavorite(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksRepository.getListTracksFavorite()
    }

    override suspend fun isTrackFavorite(trackId: Long): Boolean {
        return favoriteTracksRepository.isTrackFavorite(trackId)
    }
}
