package com.example.githubfetch.data.api.user

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

sealed class NetworkError : Exception() {
    data class CustomError(val errorMessage: String) : NetworkError()
}

class UserRemoteDatasourceImpl(
    private val context: CoroutineContext,
    private val client: HttpClient
): UserRemoteDatasource {

    private val baseUrl: String = "https://api.github.com/"

    private suspend inline fun <reified T> call(path: String): T {
        return try {
            withContext(context) {
                val response: T = client.get(path){
                    headers {
                        // you can add your own api key here for better performance 
//                        append("Authorization", "Bearer ")
                    }
                }.body()
                response
            }
        } catch (e: Exception) {
            throw NetworkError.CustomError(e.message ?: "An unknown error occurred.")
        }
    }

    override suspend fun getUsers(query: String, page: Int, perPage: Int): UsersResponse {
        val url = "${baseUrl}search/users?q=$query&page=$page&per_page=$perPage"
        return call(url)
    }

    override suspend fun getUserRepoCount(userName: String): UserRepoDTO {
        return call("${baseUrl}users/$userName")
    }

}