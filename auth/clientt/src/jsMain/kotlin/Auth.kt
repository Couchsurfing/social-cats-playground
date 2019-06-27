package com.nicolasmilliard.socialcats.auth

actual class Auth {
    actual val currentUser: AuthUser?
        get() = TODO("not implemented")

    actual fun addAuthStateListener(listener: AuthStateListener) {
        TODO("not implemented")
    }

    actual fun removeAuthStateListener(listener: AuthStateListener) {
        TODO("not implemented")
    }

    actual suspend fun getIdToken(): String? {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    actual fun signOut() {
        TODO("not implemented")
    }
}
