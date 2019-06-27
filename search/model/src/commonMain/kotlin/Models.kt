package com.nicolasmilliard.socialcats.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String
)

@Serializable
data class SearchUsersResult(
    val totalHits: Long,
    val users: List<User>
)
