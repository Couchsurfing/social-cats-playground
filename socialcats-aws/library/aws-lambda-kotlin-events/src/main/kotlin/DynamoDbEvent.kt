package com.nicolasmilliard.services.lambda.runtime.events

import com.nicolasmilliard.services.lambda.runtime.events.model.dynamodb.Identity
import com.nicolasmilliard.services.lambda.runtime.events.model.dynamodb.StreamRecord
import kotlinx.serialization.Serializable

@Serializable
public data class DynamodbEvent(
    val Records: List<DynamodbStreamRecord>
)

@Serializable
public data class DynamodbStreamRecord(
    val eventSourceARN: String,
    val eventID: String,
    val eventName: String,
    val eventVersion: String,
    val eventSource: String,
    val awsRegion: String,
    val dynamodb: StreamRecord,
    val userIdentity: Identity,
)
