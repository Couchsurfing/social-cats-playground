package com.nicolasmilliard.pushnotification

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.nicolasmilliard.android.datastore.DataStoreRepository
import com.nicolasmilliard.socialcatsaws.api.bearer
import com.nicolasmilliard.socialcatsaws.auth.Auth
import com.nicolasmilliard.socialcatsaws.pushnotification.SendRegTokenApi
import com.nicolasmilliard.socialcatsaws.pushnotification.models.IdProvider
import com.nicolasmilliard.socialcatsaws.pushnotification.models.Platform
import com.nicolasmilliard.socialcatsaws.pushnotification.models.SendRegTokenRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.util.Locale

public class RegTokenService internal constructor(
  private val appVersionCode: Int,
  private val workManger: WorkManager,
  private val apiImage: SendRegTokenApi,
  private val auth: Auth,
  private val firebaseInstallations: FirebaseInstallations,
  private val firebaseMessaging: FirebaseMessaging,
  private val mainScope: CoroutineScope,
  private val store: DataStoreRepository,
) {

  public fun init() {
    mainScope.launch {
      auth.signInEvents
        .collect {
          Timber.i("SendRegTokenService Detected need to send token")
          scheduleSendToken(it.signedInState.userId, false)
        }
    }
    mainScope.launch {
      auth.signOutEvents
        .collect {
          Timber.i("Canceling SendRegWorker if any.")
          workManger.cancelAllWorkByTag(SendRegTokenWorker.TAG)
        }
    }
  }

  public suspend fun onNewDeviceIdToken() {
    val signInState = auth.getSignInState()
    if (signInState != null) {
      scheduleSendToken(signInState.userId, true)
    } else {
      Timber.i("SendRegTokenService Received new token signal while user is not signed in")
    }
  }

  private fun scheduleSendToken(userId: String, newToken: Boolean) {

    Timber.i("SendRegTokenService.scheduleSendToken()")
    val networkType = NetworkType.CONNECTED
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(networkType)
      .build()
    val request =
      OneTimeWorkRequestBuilder<SendRegTokenWorker>()
        .setInputData(
          workDataOf(
            SendRegTokenWorker.INPUT_USER_ID to userId
          )
        )
        .setConstraints(constraints)
        .addTag(SendRegTokenWorker.TAG)
        .build()

    workManger.enqueueUniqueWork(SendRegTokenWorker.TAG, if (newToken) ExistingWorkPolicy.REPLACE else ExistingWorkPolicy.KEEP, request)
  }

  public suspend fun syncToken(userId: String): SyncTokenResult {
    val signedInState = auth.getSignInStateForUser(userId)
    if (signedInState == null) {
      Timber.w("SendRegTokenWorker while user is not signed in")
      return SyncTokenResult.Failure(false)
    }

    try {
      val storeKey = "LAST_SAVED_TOKEN#$userId"
      val lastSavedToken = store.getString(storeKey).first()

      val token = firebaseMessaging.token.await()
      if (token != lastSavedToken) {
        val instanceId = firebaseInstallations.id.await()
        sendToken(signedInState.accessToken, instanceId, token)
        store.writeString(storeKey, token)
      } else {
        Timber.w("SendRegTokenService Registration token is already up to date")
      }
    } catch (e: IOException) {
      Timber.w(e, "Error while trying to send token")
      return SyncTokenResult.Failure(true)
    } catch (e: HttpException) {
      return if (e.code() in 500..599) {
        Timber.w(e, "Backend error while trying to send token")
        SyncTokenResult.Failure(true)
      } else {
        Timber.e(e, "Error while trying to send token")
        SyncTokenResult.Failure(false)
      }
    }
    return SyncTokenResult.Success
  }

  private suspend fun sendToken(accessToken: String, instanceId: String, regToken: String) {
    Timber.i("SendRegTokenService.sendToken()")
    val request = SendRegTokenRequest(
      instanceId,
      regToken,
      IdProvider.FCM,
      Platform.ANDROID,
      appVersionCode,
      Locale.getDefault().toLanguageTag()
    )
    apiImage.sendToken(bearer(accessToken), request)
  }

  public sealed class SyncTokenResult {
    public object Success : SyncTokenResult()
    public data class Failure(val canBeRetried: Boolean) : SyncTokenResult()
  }
}
