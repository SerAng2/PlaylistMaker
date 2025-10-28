package com.example.my.domain.usecase

import com.example.my.data.mapper.MapperTrackResponseToTrack.mapTrackResponseToTrack
import com.example.my.data.repository.ITunesRepository
import com.example.my.domain.models.Track

class SearchSongsUseCase(
    private val iTunesRepository: ITunesRepository
) {
    suspend operator fun invoke(term: String): List<Track> {
        return iTunesRepository.searchSongs(term).map { dto -> mapTrackResponseToTrack(dto) }
    }
}