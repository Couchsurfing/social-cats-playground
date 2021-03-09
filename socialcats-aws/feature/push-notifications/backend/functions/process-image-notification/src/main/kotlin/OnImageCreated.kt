package com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions

import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.Schema
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class OnImageCreated(private val appComponent: AppComponent = DaggerAppComponent.create()) :
    RequestJacksonHandler<AwsEvent, Unit> {

    private val cloudMetrics = appComponent.getCloudMetrics()
    private val imageNotificationUseCase = appComponent.getImageNotificationUseCase()

    override val inputType: Class<AwsEvent>
        get() = AwsEvent::class.java

    override val mapper: ObjectMapper
        get() = appComponent.getObjectMapper()

    override fun handleRequest(input: AwsEvent, context: Context) {
        logger.debug { "Event received: $input" }
        cloudMetrics.putProperty("RequestId", context.awsRequestId)

        val userIds =
            input.detail.records.map { it.dynamodb.newImage[Schema.ImageItem.Attributes.USER_ID]!!.s }
        imageNotificationUseCase.handleNewImagesCreated(userIds)

        cloudMetrics.flush()
    }
}
