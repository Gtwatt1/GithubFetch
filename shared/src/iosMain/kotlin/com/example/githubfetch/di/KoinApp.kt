package com.example.githubfetch.di

import com.example.githubfetch.domain.user.User
import com.example.githubfetch.data.db.DatabaseDriverFactory
import com.example.githubfetch.data.db.IOSDatabaseDriverFactory
import com.example.githubfetch.data.db.recentSearch.LocalRecentSearchDataSource
import com.example.githubfetch.data.db.recentSearch.LocalRecentSearchDataSourceImpl
import com.example.githubfetch.data.db.user.LocalUserDatasource
import com.example.githubfetch.data.db.user.LocalUserDatasourceImpl
import com.example.githubfetch.domain.user.UserUseCase
import com.example.githubfetch.domain.recentSearch.RecentSearch
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module

class KoinApp: KoinComponent {
    private val userUseCase: UserUseCase by inject()

    private val iOSModule = module {
        single<DatabaseDriverFactory> { IOSDatabaseDriverFactory() }
        single<LocalUserDatasource> { LocalUserDatasourceImpl(get()) }
        single<LocalRecentSearchDataSource> { LocalRecentSearchDataSourceImpl(get()) }

    }
    fun initKoin() {
        startKoin() {
            modules(appModule + iOSModule)
        }
    }

    @NativeCoroutines
    fun getUsers(query: String, page: Int, perPage: Int): Flow<List<User>> {
        return userUseCase.getUsersWithRepoCount(query, page, perPage)
    }

    @NativeCoroutines
     fun getRecentSearches(): Flow<List<RecentSearch>> = flow {
        emitAll(userUseCase.getRecentSearches())
    }

}