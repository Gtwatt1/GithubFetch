package com.example.githubfetch.data.api.user
import com.example.githubfetch.domain.user.User
import com.example.githubfetch.data.db.user.UserEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UsersResponse(
    @SerialName("total_count")
    val totalCount: Int,

    @SerialName("incomplete_results")
    val incompleteResults: Boolean,
    @SerialName("items")
    val users: List<UserDTO>
)

@Serializable
data class UserDTO (
    val id: Int,
    @SerialName("login")
    val userName: String,
    @SerialName("avatar_url")
    val avatarUrl: String
)

fun UserDTO.toDomain(): User = User(
    id = this.id,
    userName = this.userName,
    avatarUrl = this.avatarUrl,
    repoCount = null
)

fun UserDTO.toEntity(): UserEntity = UserEntity(
    id = this.id.toLong(),
    userName = this.userName,
    avatarUrl = this.avatarUrl,
    repoCount = null
)