package com.example.githubfetch.data.db.user

interface LocalUserDatasource {
    suspend fun get(query: String): List<UserEntity>
    suspend fun save(users: List<UserEntity>)
    suspend fun update(userId: Int, repoCount: Int)
}