package com.nicolasmilliard.socialcats.auth

expect class Auth {
    val currentUser: AuthUser?
    suspend fun getIdToken(): String?
    fun addAuthStateListener(listener: AuthStateListener)
    fun removeAuthStateListener(listener: AuthStateListener)
    fun signOut()
}

interface AuthStateListener {
    fun onAuthStateChanged(auth: Auth)
}

interface IdTokenListener {
    fun onIdTokenChanged(auth: Auth)
}

data class AuthUser(
    val uid: String,
    val displayName: String?,
    val photoUrl: String?,
    val email: String?,
    val phoneNumber: String?
)
