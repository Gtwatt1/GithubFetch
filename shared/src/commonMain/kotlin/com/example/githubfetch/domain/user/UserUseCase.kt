package com.example.githubfetch.domain.user

import com.example.githubfetch.domain.recentSearch.RecentSearch
import kotlinx.coroutines.flow.Flow

interface UserUseCase {

    fun getUsersWithRepoCount(query: String, page: Int, perPage: Int): Flow<List<User>>
    suspend fun getRecentSearches():  Flow<List<RecentSearch>>
}