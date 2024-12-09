package com.example.githubfetch.data.repository.recentSearch

import com.example.githubfetch.data.db.recentSearch.LocalRecentSearchDataSource
import com.example.githubfetch.domain.recentSearch.RecentSearch
import kotlinx.coroutines.flow.Flow

class RecentSearchRepositoryImpl(
    private val localDatasource: LocalRecentSearchDataSource,
) : RecentSearchRepository {

    override suspend fun get(): Flow<List<RecentSearch>> {
        return localDatasource.get()
    }
    override suspend fun save(query: String) {
        localDatasource.save(query)
    }

}