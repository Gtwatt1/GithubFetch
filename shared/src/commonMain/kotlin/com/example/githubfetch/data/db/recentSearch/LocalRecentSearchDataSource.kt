package com.example.githubfetch.data.db.recentSearch

import com.example.githubfetch.domain.recentSearch.RecentSearch
import kotlinx.coroutines.flow.Flow

interface LocalRecentSearchDataSource {
    suspend fun get(): Flow<List<RecentSearch>>
    suspend fun save(query: String)
}