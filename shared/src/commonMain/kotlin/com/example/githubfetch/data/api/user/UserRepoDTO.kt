package com.example.githubfetch.data.api.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRepoDTO (
    @SerialName("public_repos")
    val repoCount: Int
)