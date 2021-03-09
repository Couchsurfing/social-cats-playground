package com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nicolasmilliard.socialcatsaws.backend.pushnotification.DeviceNotification
import com.nicolasmilliard.socialcatsaws.backend.pushnotification.Notification
import com.nicolasmilliard.socialcatsaws.eventpublisher.BatchEventPublisher
import com.nicolasmilliard.socialcatsaws.eventpublisher.Event
import com.nicolasmilliard.socialcatsaws.eventregistry.EventRegistry
import com.nicolasmilliard.socialcatsaws.profile.repository.UsersRepository
import javax.inject.Inject

class ImageNotificationUseCase @Inject constructor(
  private val userStore: UsersRepository,
  private val publisher: BatchEventPublisher,
  private val mapper: ObjectMapper = jacksonObjectMapper()
) {

  fun handleNewImagesCreated(userIds: List<String>) {
    val notification = Notification(
      title = "SocialCats",
      body = "New Image Successfully stored",
      analyticsLabel = "new_image_stored"
    )
    val deviceNotifications = userIds.flatMap { userId ->
      val deviceTokens = userStore.getDeviceTokens(userId, 25, null)
      val deviceNotifications = deviceTokens.tokens.map { token ->
        DeviceNotification(registrationToken = token, userId = userId, notification = notification)
      }
      deviceNotifications
    }.map {
      Event(
        EventRegistry.EventSource.NewImageNotificationProcessing,
        EventRegistry.EventType.DeviceNotification.EventDetailType,
        emptyList(),
        mapper.writeValueAsString(it)
      )
    }
    publisher.publish(deviceNotifications)
  }
}
