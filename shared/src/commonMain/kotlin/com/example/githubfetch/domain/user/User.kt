package com.example.githubfetch.domain.user

import com.example.githubfetch.data.db.user.UserEntity

data class User(
    val id: Int,
    val userName: String,
    val avatarUrl: String,
    val repoCount: Int?
)

fun User.toEntity(repoCount: Int): UserEntity = UserEntity(
    id = this.id.toLong(),
    userName = this.userName,
    avatarUrl = this.avatarUrl,
    repoCount = repoCount.toLong()
)