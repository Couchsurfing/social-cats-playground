package com.nicolasmilliard.socialcatsaws.backend.pushnotification

public interface PushNotificationService {
  public fun sendNotifications(deviceNotifications: List<DeviceNotification>): List<SendNotificationResult>
}

public data class DeviceNotification(
  val registrationToken: String,
  val userId: String,
  val notification: Notification
)

public data class Notification(
  val imageUrl: String? = null,
  val title: String,
  val body: String,
  val analyticsLabel: String,
)

public interface CanBeRetried {
  public val canBeRetriedInSeconds: Long?
}

public sealed class SendNotificationResult {
  public data class Succeed(val messageId: String) : SendNotificationResult()
  public object RegistrationTokenNotRegistered : SendNotificationResult()
  public data class ClientConfigError(val sourceCode: String) : SendNotificationResult()
  public data class QuotaExceeded(override val canBeRetriedInSeconds: Long?) : SendNotificationResult(), CanBeRetried
  public data class Unavailable(val sourceCode: String, override val canBeRetriedInSeconds: Long?) : SendNotificationResult(), CanBeRetried
}
