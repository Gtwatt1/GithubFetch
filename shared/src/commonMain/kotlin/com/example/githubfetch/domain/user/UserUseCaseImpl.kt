package com.example.githubfetch.domain.user

import com.example.githubfetch.data.repository.recentSearch.RecentSearchRepository
import com.example.githubfetch.data.repository.user.UserRepository
import com.example.githubfetch.domain.recentSearch.RecentSearch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserUseCaseImpl(
    private val userRepository: UserRepository,
    private val recentSearchRepository: RecentSearchRepository
) : UserUseCase {

    override fun getUsersWithRepoCount(query: String, page: Int, perPage: Int): Flow<List<User>> =
        flow {

            val users = userRepository.get(query, page, perPage)
            emit(users)

            val usersWithRepoCounts = coroutineScope {
                users.map { user ->
                    async {
                        try {
                            val repoCount =
                                userRepository.getUserRepoCount(user.userName, user.id)
                            user.copy(repoCount = repoCount)
                        } catch (e: Exception) {
                            user.copy(repoCount = 0)
                        }
                    }
                }.awaitAll()
            }
            recentSearchRepository.save(query)
            emit(usersWithRepoCounts)
        }

    override suspend fun getRecentSearches(): Flow<List<RecentSearch>> = recentSearchRepository.get()

}
