package com.example.githubfetch.data.db.user.repository

import com.example.githubfetch.data.api.user.NetworkError
import com.example.githubfetch.data.api.user.UserDTO
import com.example.githubfetch.data.api.user.UserRemoteDatasource
import com.example.githubfetch.data.api.user.UserRepoDTO
import com.example.githubfetch.data.api.user.UsersResponse
import com.example.githubfetch.data.api.user.toDomain
import com.example.githubfetch.data.db.user.LocalUserDatasource
import com.example.githubfetch.data.db.user.UserEntity
import com.example.githubfetch.data.db.user.toDomain
import com.example.githubfetch.data.repository.user.UserRepositoryImpl
import com.example.githubfetch.domain.user.User
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UserRepositoryImplTest {
    private val localUserDatasource = LocalUserDatasourceMock()
    private val remoteUserDatasource = UserRemoteDatasourceMock()
    private val repository = UserRepositoryImpl(localUserDatasource, remoteUserDatasource)

    @Test
    fun `get should return users from remote data source and save to local data source`() = runTest {
        val userId1 = 1
        val userId2 = 2
        val userName = "john_doe"
        val avatarUrl = "http://avatar.url"

        val remoteUsers = listOf(
            UserDTO(userId1, userName, avatarUrl),
            UserDTO(userId2, userName, avatarUrl)
        )

        remoteUserDatasource.getUsersResponse = { _, _ , _ ->
            UsersResponse(2,
            false,
             remoteUsers)
        }

        val localSavedUsers = mutableListOf<UserEntity>()
        localUserDatasource.saveResponse = { users -> localSavedUsers.addAll(users) }

        val result = repository.get(userName, 1, 10)

        assertEquals(remoteUsers.map { it.toDomain() }, result)
        assertEquals(remoteUsers.size, localSavedUsers.size)
    }

    @Test
    fun `get should return users from local data source when remote API fails`() = runTest {
        val userId1 = 1
        val userName = "john_doe"
        val avatarUrl = "http://avatar.url"
        val repoCount = 10

        val localUsers = listOf(
            UserEntity(userId1.toLong(), userName, avatarUrl, repoCount.toLong())
        )

        localUserDatasource.getResponse = { localUsers }

        val result = repository.get(userName, 1, 10)

        assertEquals(localUsers.map { it.toDomain() }, result)
    }
//
    @Test
    fun `getUserRepoCount should update repo count in local data source`() = runTest {
        val userName = "john_doe"
        val userId = 1
        val newRepoCount = 20

        remoteUserDatasource.getUserRepoCountResponse = { UserRepoDTO(newRepoCount) }

        var updatedRepoCount: Int? = null
        localUserDatasource.updateResponse = { id, repoCount ->
            updatedRepoCount = repoCount.toInt()
        }

        val result = repository.getUserRepoCount(userName, userId)

        assertEquals(newRepoCount, result)
        assertEquals(newRepoCount, updatedRepoCount)
    }

    @Test
    fun `get should return error when remote API returns failure and local data source also fails`() = runTest {

        // Call the use case method
        val result = repository.get("john_doe", 1, 10)

        // Verify that the result is a failure due to both remote and local failures
        assertEquals(0, result.size)
    }

    @Test
    fun `save should persist users to local data source`() = runTest {
        val userId = 1
        val userName = "john_doe"
        val avatarUrl = "http://avatar.url"
        val repoCount = 10

        val usersToSave = listOf(
            User(userId, userName, avatarUrl, repoCount)
        )

        // Mock local data source save call
        var savedUsers: List<UserEntity> = emptyList()
        localUserDatasource.saveResponse = { users -> savedUsers = users }

        repository.save(usersToSave)

        assertEquals(usersToSave.first().userName, savedUsers.map { it.toDomain() }.first().userName)
    }
}

class LocalUserDatasourceMock : LocalUserDatasource {
    var getResponse: (suspend (String) -> List<UserEntity>) = { listOf() }
    var saveResponse: (suspend (List<UserEntity>) -> Unit) = {}
    var updateResponse: (suspend (Int, Long) -> Unit) = { _, _ -> throw NotImplementedError() }

    override suspend fun get(query: String): List<UserEntity> {
        return getResponse(query)
    }

    override suspend fun save(users: List<UserEntity>) {
        saveResponse(users)
    }

    override suspend fun update(userId: Int, repoCount: Int) {
        updateResponse(userId, repoCount.toLong())
    }
}

class UserRemoteDatasourceMock : UserRemoteDatasource {
    var getUsersResponse: (suspend (String, Int, Int) -> UsersResponse) = { _, _, _ -> throw NetworkError.CustomError("") }
    var getUserRepoCountResponse: (suspend (String) -> UserRepoDTO) = { _ -> throw NotImplementedError()  }

    override suspend fun getUsers(query: String, page: Int, perPage: Int): UsersResponse {
        return getUsersResponse(query, page, perPage)
    }

    override suspend fun getUserRepoCount(userName: String): UserRepoDTO {
        return getUserRepoCountResponse(userName)
    }
}