package com.nicolasmilliard.socialcatsaws.auth

import android.app.Activity
import android.content.Intent
import com.amplifyframework.auth.AuthCategory
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.InitializationStatus
import com.amplifyframework.hub.HubCategory
import com.amplifyframework.hub.HubChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class CognitoAuth(
  private val scope: CoroutineScope,
  private val auth: AuthCategory,
  private val hub: HubCategory
) : AuthProvider {

  override val authStates: StateFlow<AuthState> get() = authStateFlow()

  private fun authStateFlow() = callbackFlow {
    val token = hub.subscribe(HubChannel.AUTH) { event ->
      when (event.name) {
        InitializationStatus.SUCCEEDED.name -> {
          Timber.i("Auth successfully initialized")
        }
        InitializationStatus.FAILED.name -> {
          Timber.e("Auth failed to succeed")
          throw IllegalStateException("Cognito Auth Failed to initialize")
        }
        else -> {
          offer(Unit)
        }
      }
    }
    offer(Unit)
    awaitClose {
      hub.unsubscribe(token)
    }
  }.map {
    getCurrentState()
  }.stateIn(scope, SharingStarted.WhileSubscribed(), AuthState.Initializing)

  private suspend fun getCurrentState(): AuthState {
    val session = fetchAuthSession()
    return if (session.isSignedIn) {
      val userId = session.identityId.value!!
      val poolTokensResult = session.userPoolTokens
      if (poolTokensResult.type == AuthSessionResult.Type.FAILURE) {
        AuthState.SessionExpired(userId)
      } else {
        AuthState.SignedIn(userId, poolTokensResult.value!!.accessToken)
      }
    } else {
      AuthState.SignOut
    }
  }

  override suspend fun signInWithWebUI(callingActivity: Activity): Boolean =
    suspendCoroutine { cont ->
      auth.signInWithWebUI(
        callingActivity,
        {
          cont.resume(it.isSignInComplete)
        },
        {
          if (it is AuthException.UserCancelledException) {
            cont.resume(false)
          } else {
            cont.resumeWithException(it)
          }
        }
      )
    }

  override suspend fun signOut(): Unit = suspendCoroutine { cont ->
    auth.signOut({ cont.resume(Unit) }, { cont.resumeWithException(it) })
  }

  suspend fun fetchAuthSession(): AWSCognitoAuthSession = suspendCoroutine { cont ->
    auth.fetchAuthSession(
      {
        cont.resume(it as AWSCognitoAuthSession)
      },
      {
        cont.resumeWithException(it)
      }
    )
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == AWSCognitoAuthPlugin.WEB_UI_SIGN_IN_ACTIVITY_CODE) {
      auth.handleWebUISignInResponse(data)
    }
  }
}
