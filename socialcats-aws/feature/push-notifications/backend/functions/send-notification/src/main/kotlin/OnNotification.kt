package com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class OnNotification(appComponent: AppComponent = DaggerAppComponent.create()) :
    RequestHandler<SQSEvent, Unit> {

    private val cloudMetrics = appComponent.getCloudMetrics()
    private val useCase = appComponent.getSendNotificationUseCase()
    private val eventSource = appComponent.getEventSource()

    override fun handleRequest(input: SQSEvent, context: Context) {
        logger.debug { "Event received: $input" }
        cloudMetrics.putProperty("RequestId", context.awsRequestId)
        try {

            val sendResults = useCase.sendAll(input.records.map { it.body })
            val (successfulNotifications, failedNotifications) = input.records.asSequence().mapIndexed { index, sqsMessage ->
                sqsMessage to sendResults[index]
            }.toMap().asSequence().partition { it.value is SendNotificationUseCase.SendResult.Completed }

            logger.info { "Successful Notifications: ${successfulNotifications.size}, failed: ${failedNotifications.size}" }
            if (!failedNotifications.isNullOrEmpty()) {
                if (!successfulNotifications.isNullOrEmpty()) {
                    // If there was failures we handle marking successful event manually
                    eventSource.markEventAsProcessed(successfulNotifications.map { it.key })
                }

                eventSource.updateEventTimeoutVisibility(failedNotifications
                    .map { it.key to (it.value as SendNotificationUseCase.SendResult.RetryableFailure).canBeRetriedInSeconds }
                    .toMap())
                throw FailedToSendNotifications()
            }
        } finally {
            cloudMetrics.flush()
        }
    }
}

class FailedToSendNotifications : Exception()
