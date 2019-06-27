package com.nicolasmilliard.socialcats.searchapi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.*
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.response.respond
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val Logger: Logger =
  LoggerFactory.getLogger("com.nicolasmilliard.socialcats.searchapi.firebaseAuth")

const val FirebaseAuthKey = "FirebaseAuth"
private const val scheme = "Bearer"

data class PrincipalToken(val value: FirebaseToken) : Principal

class FirebaseAuthProvider internal constructor(
  config: Configuration
) : AuthenticationProvider(config) {

  private val firebaseAuth = config.firebaseAuth

  private var validateFunc = config.validate

  class Configuration internal constructor(name: String?) :
    AuthenticationProvider.Configuration(name) {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var validate: suspend ApplicationCall.(FirebaseToken) -> Principal?
    internal fun build() = FirebaseAuthProvider(this)
  }

  suspend fun verifyIdToken(call: ApplicationCall, tokenStr: String): Principal? =
    withContext(Dispatchers.IO) {
      val firebaseToken = firebaseAuth.verifyIdToken(tokenStr)
      call.validateFunc(firebaseToken)
    }
}

/**
 * FirebaseToken
 *
 * @param name [FirebaseAuthProvider]
 */
fun Authentication.Configuration.firebaseAuth(
  name: String = "",
  configure: FirebaseAuthProvider.Configuration.() -> Unit
) {
  val provider = FirebaseAuthProvider.Configuration(name).apply(configure).build()
  provider.auth()
  register(provider)
}

internal fun FirebaseAuthProvider.auth() {
  pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
    val authHeader = call.request.parseAuthorizationHeader()
    if (authHeader == null) {
      context.bearerChallenge(AuthenticationFailedCause.NoCredentials)
      return@intercept
    }

    val principal = try {
      authHeader.bearerBlob?.let { verifyIdToken(call, it) }
    } catch (cause: FirebaseAuthException) {
      Logger.trace("Firebase Auth verifying token failed", cause)
      null
    } catch (cause: Throwable) {
      Logger.error("Firebase Auth unknown error", cause)
      context.error(
        FirebaseAuthKey,
        AuthenticationFailedCause.Error("Failed to verify auth token due to $cause")
      )
      null
    }

    if (principal != null) {
      context.principal(principal)
    } else {
      context.bearerChallenge(AuthenticationFailedCause.InvalidCredentials)
    }
  }
}

private fun AuthenticationContext.bearerChallenge(
  failedCause: AuthenticationFailedCause
) = challenge(FirebaseAuthKey, failedCause) {
  call.respond(UnauthorizedResponse(HttpAuthHeader.bearerAuthChallenge))
  it.complete()
}

private val HttpAuthHeader.Companion.bearerAuthChallenge: HttpAuthHeader.Parameterized
  get() =
    HttpAuthHeader.Parameterized(scheme, mapOf())

private val HttpAuthHeader.bearerBlob: String?
  get() = when {
    this is HttpAuthHeader.Single && authScheme.toLowerCase() == scheme.toLowerCase() -> blob
    else -> null
  }
