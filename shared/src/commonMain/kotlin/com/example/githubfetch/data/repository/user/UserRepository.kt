package com.example.githubfetch.data.repository.user

import com.example.githubfetch.domain.user.User

interface UserRepository {
    suspend fun get(query: String, page: Int, perPage: Int): List<User>
    suspend fun getUserRepoCount(userName: String, userId: Int): Int
    suspend fun save(users: List<User>)
}