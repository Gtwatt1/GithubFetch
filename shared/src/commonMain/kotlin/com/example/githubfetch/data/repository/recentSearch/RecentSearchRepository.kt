package com.example.githubfetch.data.repository.recentSearch

import com.example.githubfetch.domain.recentSearch.RecentSearch
import kotlinx.coroutines.flow.Flow

interface RecentSearchRepository {
    suspend fun get(): Flow<List<RecentSearch>>
    suspend fun save(query: String)
}