package com.nicolasmilliard.socialcatsaws.backend.pushnotification

import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.AndroidFcmOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MessagingErrorCode
import com.google.firebase.messaging.Notification
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
public class FcmNotifications(private val fcm: FirebaseMessaging) : PushNotificationService {

  override fun sendNotifications(deviceNotifications: List<DeviceNotification>): List<SendNotificationResult> {
    val messages = deviceNotifications.map {
      buildMessage(it)
    }
    val batchResponse = fcm.sendAll(messages)
    logger.info { "FCM successfully sent  ${batchResponse.successCount} and failed: ${batchResponse.failureCount}" }
    return batchResponse.responses.map {
      if (it.isSuccessful) {
        SendNotificationResult.Succeed(it.messageId)
      } else {
        it.exception!!.toResult()
      }
    }
  }

  private fun buildMessage(deviceNotification: DeviceNotification): Message {
    val notification = deviceNotification.notification
    return Message.builder()
      .setNotification(
        Notification.builder()
          .setTitle(notification.title)
          .setBody(notification.body)
          .setImage(notification.imageUrl).build()
      )
      .setAndroidConfig(
        AndroidConfig.builder()
          .setPriority(AndroidConfig.Priority.NORMAL)
          .setFcmOptions(AndroidFcmOptions.withAnalyticsLabel(notification.analyticsLabel))
          .build()
      )
      .setToken(deviceNotification.registrationToken)
      .build()
  }

  private fun FirebaseMessagingException.toResult(): SendNotificationResult {
    return when (messagingErrorCode!!) {
      MessagingErrorCode.SENDER_ID_MISMATCH,
      MessagingErrorCode.THIRD_PARTY_AUTH_ERROR,
      MessagingErrorCode.INVALID_ARGUMENT -> {
        logger.error(this) { "Client Error while sending notification" }
        SendNotificationResult.ClientConfigError(messagingErrorCode.toString())
      }
      MessagingErrorCode.QUOTA_EXCEEDED -> {
        logger.warn(this) { "Error while sending notification, Quota exceeded" }
        SendNotificationResult.QuotaExceeded(getRetryAfter())
      }
      MessagingErrorCode.INTERNAL,
      MessagingErrorCode.UNAVAILABLE -> {
        logger.error(this) { "Fcm Error while sending notification" }
        SendNotificationResult.Unavailable(messagingErrorCode.toString(), getRetryAfter())
      }
      MessagingErrorCode.UNREGISTERED -> SendNotificationResult.RegistrationTokenNotRegistered
    }
  }

  private fun FirebaseMessagingException.getRetryAfter(): Long? {
    val retryAfter = httpResponse.headers["Retry-After"]
    if (retryAfter != null) {
      return retryAfter as Long
    }
    return null
  }
}
