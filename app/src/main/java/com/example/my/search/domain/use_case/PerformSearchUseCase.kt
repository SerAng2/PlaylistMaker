package com.example.my.search.domain.use_case

import com.example.my.common.domain.model.Track

interface PerformSearchUseCase {

    suspend fun performSearch(term: String): List<Track>
}