package com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent
import com.fasterxml.jackson.annotation.JsonProperty

data class AwsEvent (
    val id: String,
    val region: String,
    val account: String,
    @JsonProperty("detail-type")
    val detailType: String,
    val detail: DynamodbEvent,
    val resources: List<String>,
    val source: String,
    val time: String,
    val version: String,
)