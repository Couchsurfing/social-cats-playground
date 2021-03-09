package com.nicolasmilliard.socialcatsaws.auth

import android.app.Activity
import android.content.Intent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningReduce

public class Auth(private val provider: AuthProvider) {

  public val authStates: Flow<AuthState> = provider.authStates

  // States for signing in from not an Unknown state
  public val signInEvents: Flow<SignedInEvent> = authStates
    .filterNot { it is AuthState.Initializing }
    .map { Pair(it is AuthState.SignedIn, it) } // false (not signed in)
    .runningReduce { accumulator, value -> Pair(!accumulator.first && value.first, value.second) } // false
    .drop(1)
    .filter { it.first }
    .map { SignedInEvent(it.second as AuthState.SignedIn) }
  public val signOutEvents: Flow<Boolean> = authStates
    .filterNot { it is AuthState.Initializing }
    .map { it is AuthState.SignedIn }
    .runningReduce { accumulator, value -> accumulator && !value }
    .drop(1)
    .filter { it }

  public data class SignedInEvent(val signedInState: AuthState.SignedIn)

  public suspend fun getSignInState(): AuthState.SignedIn? {
    val state = provider.authStates.filterNot { it is AuthState.Initializing }.first()
    if (state is AuthState.SignedIn) {
      return state
    }
    return null
  }

  public suspend fun getSignInStateForUser(userId: String): AuthState.SignedIn? {
    val state = getSignInState()
    if (state != null && state.userId == userId) {
      return state
    }
    return null
  }

  public suspend fun signInWithWebUI(activity: Activity) {
    provider.signInWithWebUI(activity)
  }

  public suspend fun signOut(): Unit = provider.signOut()

  public fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    provider.onActivityResult(requestCode, resultCode, data)
  }
}

public sealed class AuthState {
  public object Initializing : AuthState()
  public data class SignedIn(
    val userId: String,
    val accessToken: String
  ) : AuthState()

  public object SignOut : AuthState()
  public data class SessionExpired(val userId: String) : AuthState()
}

public interface AuthProvider {

  public val authStates: StateFlow<AuthState>

  public suspend fun signInWithWebUI(callingActivity: Activity): Boolean
  public suspend fun signOut()
  public fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}
