package com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions

import com.amazonaws.services.lambda.runtime.tests.EventLoader
import com.nicolasmilliard.socialcatsaws.backend.pushnotification.SendNotificationResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OnNotificationTest {

    private val fakeContext = FakeContext()

    @Test
    fun testSuccessFullNotification() {
        val fakeUsersRepository = FakeUsersRepository()
        val fakeSqsEventSource = FakeSqsEventSource()
        val fakePushNotification = FakePushNotification()

        fakePushNotification.results.add(listOf(SendNotificationResult.Succeed("messageId")))

        val event = EventLoader.loadSQSEvent("sqsevent.json")
        val appComponent: AppComponent = DaggerTestAppComponent.builder()
            .fakeUserRepository(fakeUsersRepository)
            .fakeSqsEventSource(fakeSqsEventSource)
            .fakePushNotification(fakePushNotification)
            .build()

        val handler = OnNotification(appComponent)
        handler.handleRequest(event, fakeContext)

        assertEquals(0, fakeSqsEventSource.processedEvents.size)
        assertEquals(0, fakeSqsEventSource.udpatedEventsTimeout.size)
        assertEquals(0, fakeUsersRepository.deletedDevices.size)
    }

    @Test
    fun testObsoleteToken() {
        val fakeUsersRepository = FakeUsersRepository()
        val fakeSqsEventSource = FakeSqsEventSource()
        val fakePushNotification = FakePushNotification()

        fakePushNotification.results.add(listOf(SendNotificationResult.RegistrationTokenNotRegistered))

        val event = EventLoader.loadSQSEvent("sqsevent.json")
        val appComponent: AppComponent = DaggerTestAppComponent.builder()
            .fakeUserRepository(fakeUsersRepository)
            .fakeSqsEventSource(fakeSqsEventSource)
            .fakePushNotification(fakePushNotification)
            .build()

        val handler = OnNotification(appComponent)
        handler.handleRequest(event, fakeContext)

        assertEquals(0, fakeSqsEventSource.processedEvents.size)
        assertEquals(0, fakeSqsEventSource.udpatedEventsTimeout.size)
        assertEquals(1, fakeUsersRepository.deletedDevices.size)
    }

    @Test
    fun testRetry() {
        val fakeUsersRepository = FakeUsersRepository()
        val fakeSqsEventSource = FakeSqsEventSource()
        val fakePushNotification = FakePushNotification()

        fakePushNotification.results.add(listOf(SendNotificationResult.QuotaExceeded(10)))

        val event = EventLoader.loadSQSEvent("sqsevent.json")
        val appComponent: AppComponent = DaggerTestAppComponent.builder()
            .fakeUserRepository(fakeUsersRepository)
            .fakeSqsEventSource(fakeSqsEventSource)
            .fakePushNotification(fakePushNotification)
            .build()

        val handler = OnNotification(appComponent)
        assertThrows<FailedToSendNotifications> {
            handler.handleRequest(event, fakeContext)
        }

        assertEquals(0, fakeSqsEventSource.processedEvents.size)
        assertEquals(1, fakeSqsEventSource.udpatedEventsTimeout.size)
        assertEquals(0, fakeUsersRepository.deletedDevices.size)
    }

    @Test
    fun testMixedEventFullNotification() {
        val fakeUsersRepository = FakeUsersRepository()
        val fakeSqsEventSource = FakeSqsEventSource()
        val fakePushNotification = FakePushNotification()

        fakePushNotification.results.add(listOf(
            SendNotificationResult.Succeed("messageId"),
            SendNotificationResult.RegistrationTokenNotRegistered,
           SendNotificationResult.QuotaExceeded(null)))


        val event = EventLoader.loadSQSEvent("sqsevents.json")
        val appComponent: AppComponent = DaggerTestAppComponent.builder()
            .fakeUserRepository(fakeUsersRepository)
            .fakeSqsEventSource(fakeSqsEventSource)
            .fakePushNotification(fakePushNotification)
            .build()

        val handler = OnNotification(appComponent)
        assertThrows<FailedToSendNotifications> {
            handler.handleRequest(event, fakeContext)
        }

        assertEquals(2, fakeSqsEventSource.processedEvents.size)
        assertEquals(1, fakeSqsEventSource.udpatedEventsTimeout.size)
        assertEquals(1, fakeUsersRepository.deletedDevices.size)
    }
}