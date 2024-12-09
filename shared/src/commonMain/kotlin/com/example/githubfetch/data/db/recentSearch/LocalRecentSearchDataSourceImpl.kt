package com.example.githubfetch.data.db.recentSearch

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.githubfetch.cache.AppDatabase
import com.example.githubfetch.data.db.DatabaseDriverFactory
import com.example.githubfetch.domain.recentSearch.RecentSearch
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalRecentSearchDataSourceImpl(
    private val databaseDriverFactory: DatabaseDriverFactory
) : LocalRecentSearchDataSource {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val queries = database.appDatabaseQueries

    override suspend fun get():
            Flow<List<RecentSearch>> {
        return queries.selectRecentSearchQueries().asFlow().mapToList(Dispatchers.IO).map { queryList ->
            queryList.map {
                RecentSearch(
                    query = it.query,
                    timeStamp = it.timestamp
                )
            }

        }
    }

    override suspend fun save(query: String) {
        queries.insertRecentSearchQuery(
            query = query,
            timestamp = getTimeMillis(),
        )
    }

}