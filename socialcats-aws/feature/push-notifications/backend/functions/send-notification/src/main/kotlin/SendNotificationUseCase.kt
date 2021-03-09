package com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nicolasmilliard.socialcatsaws.backend.pushnotification.DeviceNotification
import com.nicolasmilliard.socialcatsaws.backend.pushnotification.PushNotificationService
import com.nicolasmilliard.socialcatsaws.backend.pushnotification.SendNotificationResult
import com.nicolasmilliard.socialcatsaws.profile.repository.UserDeviceToken
import com.nicolasmilliard.socialcatsaws.profile.repository.UsersRepository
import mu.KotlinLogging
import javax.inject.Inject

private val logger = KotlinLogging.logger {}

class SendNotificationUseCase @Inject constructor(
    private val pushNotificationService: PushNotificationService,
    private val usersRepository: UsersRepository,
    private val mapper: ObjectMapper = jacksonObjectMapper()
) {

    fun sendAll(notificationsJson: List<String>): List<SendResult> {
        val deviceNotifications = notificationsJson.map {
            mapper.readValue(it, DeviceNotification::class.java)
        }

        val results = pushNotificationService.sendNotifications(deviceNotifications)
        val notifAndResult = deviceNotifications.mapIndexed { index, deviceNotification ->
            deviceNotification to results[index]
        }.toMap()
        val invalidTokens = notifAndResult.filter { it.value is SendNotificationResult.RegistrationTokenNotRegistered }

        if (invalidTokens.isNotEmpty()) deleteDevices(invalidTokens.keys)

        return notifAndResult.map {
            when (val notifResult = it.value) {
                is SendNotificationResult.ClientConfigError,
                SendNotificationResult.RegistrationTokenNotRegistered,
                is SendNotificationResult.Succeed -> SendResult.Completed
                is SendNotificationResult.QuotaExceeded -> {
                    SendResult.RetryableFailure(notifResult.canBeRetriedInSeconds)
                }
                is SendNotificationResult.Unavailable -> {
                    SendResult.RetryableFailure(notifResult.canBeRetriedInSeconds)
                }
            }
        }
    }

    private fun deleteDevices(invalidTokens: Set<DeviceNotification>) {
        try {
            usersRepository.deleteDevices(invalidTokens.map { UserDeviceToken(it.userId, it.registrationToken)  })
        } catch (e: Exception) {
            logger.error(e) { "Error while trying to remove device" }
        }
    }

    sealed class SendResult {
        object Completed : SendResult()
        data class RetryableFailure(val canBeRetriedInSeconds: Long?) : SendResult()
    }
}
