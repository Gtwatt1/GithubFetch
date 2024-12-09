package com.example.githubfetch.data.db.user

import com.example.githubfetch.domain.user.User

data class UserEntity (
    val id: Long,
    val userName: String,
    val avatarUrl: String,
    val repoCount: Long?
)

fun UserEntity.toDomain(): User = User(
    id = this.id.toInt(),
    userName = this.userName,
    avatarUrl = this.avatarUrl,
    repoCount = this.repoCount?.toInt() ?: 0
)