package com.example.playlistMaker.search.data.dto

data class SearchDto(
    val resultCount: Int?,
    val results: List<TrackDto>
 )