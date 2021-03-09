package com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions

import com.nicolasmilliard.socialcatsaws.backend.pushnotification.DeviceNotification
import com.nicolasmilliard.socialcatsaws.backend.pushnotification.PushNotificationService
import com.nicolasmilliard.socialcatsaws.backend.pushnotification.SendNotificationResult

class FakePushNotification: PushNotificationService {
    var results: ArrayDeque<List<SendNotificationResult>> = ArrayDeque()

    override fun sendNotifications(deviceNotifications: List<DeviceNotification>): List<SendNotificationResult> {
        return results.removeFirst()
    }
}