package com.example.githubfetch.data.repository.user

import com.example.githubfetch.domain.user.User
import com.example.githubfetch.data.api.user.NetworkError
import com.example.githubfetch.data.api.user.UserRemoteDatasource
import com.example.githubfetch.data.api.user.toDomain
import com.example.githubfetch.data.api.user.toEntity
import com.example.githubfetch.data.db.user.LocalUserDatasource
import com.example.githubfetch.data.db.user.toDomain
import com.example.githubfetch.domain.user.toEntity

class UserRepositoryImpl(
    private val localUserDatasource: LocalUserDatasource,
    private val remoteUserDatasource: UserRemoteDatasource
) : UserRepository {
    override suspend fun get(query: String, page: Int, perPage: Int): List<User> {
        try {
            val users = remoteUserDatasource.getUsers(query, page, perPage).users
            localUserDatasource.save(users.map { it.toEntity() })
            return users.map { it.toDomain() }
        } catch (e: NetworkError) {
            return localUserDatasource.get(query).map { it.toDomain() }
        }
    }

    override suspend fun getUserRepoCount(userName: String, userId: Int): Int {
        val repoCount = remoteUserDatasource.getUserRepoCount(userName).repoCount
        localUserDatasource.update(userId, repoCount)
        return repoCount
    }

    override suspend fun save(users: List<User>) {
        localUserDatasource.save(users.map { it.toEntity(0) })
    }

}