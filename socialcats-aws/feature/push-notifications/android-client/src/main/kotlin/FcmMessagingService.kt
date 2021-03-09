package com.nicolasmilliard.pushnotification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
public class FcmMessagingService : FirebaseMessagingService() {

  @Inject
  internal lateinit var service: RegTokenService

  override fun onNewToken(token: String): Unit = runBlocking {
    Timber.i("FcmMessagingService.onNewToken()")
    service.onNewDeviceIdToken()
  }

  override fun onMessageReceived(message: RemoteMessage) {
  }

  override fun onDeletedMessages() {
    TODO()
  }

  private fun RemoteMessage.Notification.toNotification() = Notification(body, title, channelId)
  private fun RemoteMessage.toMessage() =
    Message(messageId, messageType, collapseKey, sentTime, data, notification?.toNotification())
}

internal data class Message(
  val messageId: String?,
  val messageType: String?,
  val collapseKey: String?,
  val sentTime: Long,
  val data: Map<String, String>,
  val notification: Notification?
)

internal data class Notification(
  val body: String?,
  val title: String?,
  val channelId: String?
)
