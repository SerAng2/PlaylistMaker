package com.example.my.search.data.dto

import com.example.my.search.data.dto.TrackDto

data class SearchDto(
    val resultCount: Int?,
    val results: List<TrackDto>
 )