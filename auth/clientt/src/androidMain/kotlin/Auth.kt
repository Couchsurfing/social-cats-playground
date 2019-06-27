package com.nicolasmilliard.socialcats.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

actual class Auth(val firebaseAuth: FirebaseAuth) {

    private val authListeners = HashMap<AuthStateListener, FirebaseAuth.AuthStateListener>()

    actual val currentUser: AuthUser?
        get() = firebaseAuth.currentUser?.toAuthUser()

    actual fun addAuthStateListener(listener: AuthStateListener) {
        val authListenerFirebase =
            FirebaseAuth.AuthStateListener { listener.onAuthStateChanged(this@Auth) }
        firebaseAuth.addAuthStateListener(authListenerFirebase)
        authListeners.put(listener, authListenerFirebase)
    }

    actual fun removeAuthStateListener(listener: AuthStateListener) {
        val authStateListener = authListeners[listener]
        requireNotNull(authStateListener)
        firebaseAuth.removeAuthStateListener(authStateListener)
        authListeners.remove(listener)
    }

    actual fun signOut() {
        firebaseAuth.signOut()
    }

    actual suspend fun getIdToken(): String? {
        val currentUser = firebaseAuth.currentUser ?: return null
        return currentUser.getIdToken(false).await().token
    }
}

private fun FirebaseUser.toAuthUser(): AuthUser = AuthUser(uid, displayName, photoUrl.toString(), email, phoneNumber)
