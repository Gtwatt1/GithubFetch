package com.example.githubfetch.di

import com.example.githubfetch.data.api.user.UserRemoteDatasource
import com.example.githubfetch.data.api.user.UserRemoteDatasourceImpl
import com.example.githubfetch.data.repository.recentSearch.RecentSearchRepository
import com.example.githubfetch.data.repository.recentSearch.RecentSearchRepositoryImpl
import com.example.githubfetch.data.repository.user.UserRepository
import com.example.githubfetch.data.repository.user.UserRepositoryImpl
import com.example.githubfetch.domain.user.UserUseCase
import com.example.githubfetch.domain.user.UserUseCaseImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val appModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    useAlternativeNames = false
                })
            }
        }
    }
    single<UserRemoteDatasource> { UserRemoteDatasourceImpl( Dispatchers.IO, get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<RecentSearchRepository> { RecentSearchRepositoryImpl(get()) }
    single<UserUseCase> { UserUseCaseImpl( get(), get()) }

}