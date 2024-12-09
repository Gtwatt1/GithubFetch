package com.example.githubfetch.data.db.user

import com.example.githubfetch.cache.AppDatabase
import com.example.githubfetch.data.db.DatabaseDriverFactory

class LocalUserDatasourceImpl(
    private val databaseDriverFactory: DatabaseDriverFactory
) : LocalUserDatasource {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val queries = database.appDatabaseQueries

    override suspend fun get(query: String): List<UserEntity> {
        return queries.selectByUserName(query).executeAsList() .map {
            UserEntity(
                id = it.id,
                userName = it.userName,
                avatarUrl = it.avatarUrl,
                repoCount = it.repoCount
            )
        }
    }

    override suspend fun save(users: List<UserEntity>) {
        users.forEach {
            queries.insert(
                id = it.id,
                userName = it.userName,
                avatarUrl = it.avatarUrl,
            )
        }
    }

    override suspend fun update(userId: Int, repoCount: Int) {
        queries.updateRepoCount(repoCount = repoCount.toLong(), userId.toLong())
    }
}