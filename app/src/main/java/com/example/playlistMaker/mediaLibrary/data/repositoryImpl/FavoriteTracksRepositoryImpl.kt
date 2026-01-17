package com.example.playlistMaker.mediaLibrary.data.repositoryImpl

import com.example.playlistMaker.common.domain.model.Track
import com.example.playlistMaker.mediaLibrary.data.db.AppDatabase
import com.example.playlistMaker.mediaLibrary.data.mapper.TrackDbConvertor
import com.example.playlistMaker.mediaLibrary.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoriteTracksRepository {

    override suspend fun addTrackFavorite(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().insertTrack(trackEntity)
    }

    override suspend fun removeTrackFavorite(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().deleteTrack(trackEntity)
    }

    override fun getListTracksFavorite(): Flow<List<Track>> =
        appDatabase.trackDao().getFavoriteTracks()
            .map { tracks ->
                tracks.map { trackDbConvertor.map(it) }
                    .sortedByDescending { track -> track.trackId }

            }

    override suspend fun isTrackFavorite(trackId: Long): Boolean {
        return appDatabase.trackDao().isTrackFavorite(trackId) > 0
    }
}
