package com.example.githubfetch.data.api.user

interface UserRemoteDatasource {
    suspend fun getUsers(query: String, page: Int, perPage: Int): UsersResponse
    suspend fun getUserRepoCount(userName: String): UserRepoDTO
}