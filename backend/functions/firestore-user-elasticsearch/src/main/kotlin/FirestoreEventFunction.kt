package com.nicolasmilliard.socialcats

import com.google.cloud.functions.Context
import com.google.gson.JsonElement
import mu.KotlinLogging
import org.apache.logging.log4j.util.Strings
import java.util.*
import kotlin.collections.HashMap

private val log = KotlinLogging.logger {}

/**
 * Function triggered on Firestore documents (/databases/(default)/documents/users/{id}) modifications.
 */
class FirestoreEventFunction(
  graph: Graph = AppComponent().build()
) {
  private val searchUseCase = graph.searchUseCase

  fun onUserWritten(rawEvent: RawFirestoreEvent, context: Context) {
    log.debug {
      "Event ID: ${context.eventId()}, Resource: ${context.resource()}, Event Type: ${context.eventType()}, Timestamp: ${context.timestamp()}"
    }
    log.info { "On User written, Event: $rawEvent" }

    when (val event = rawEvent.map()) {
      is CreatedEvent -> {
        updateName(event.value)
      }
      is UpdatedEvent -> {
        if (event.hasUserNameChanged) {
          updateName(event.value)
        } else {
          log.info { "This user change is not interesting for us: ${event.updatedFields.joinToString()}" }
        }
      }
      is DeletedEvent -> searchUseCase.deleteUser(event.resourceId)
    }
  }

  private fun updateName(value: FirestoreValue) {
    val name = value.fields["name"]
    checkNotNull(name, { "Name field is mandatory" })
    searchUseCase.updateUserName(value.resourceId, value.updateTime, name as String)
  }
}

// Below are models serialized automatically from Gson.
// Due to Gson limited Kotlin support, be warn of:
// - making sure all properties are nullable
// - no default value are assigned to property
// - no delegate properties are used
// More information here: https://medium.com/@programmerr47/gson-unsafe-problem-d1ff29d4696f

data class RawFirestoreValue(
  val createTime: Date?,
  val updateTime: Date?,
  val name: String?,
  val fields: JsonElement?
)

data class RawFirestoreEvent(
  val oldValue: RawFirestoreValue?,
  val value: RawFirestoreValue?,
  val updateMask: RawUpdateMask?
)

data class RawUpdateMask(
  val fieldPaths: Set<String>?
)

// Below are Kotlin friendly models we will use to map our raw model into.

sealed class FirestoreEvent

data class CreatedEvent(
  val value: FirestoreValue
) : FirestoreEvent()

data class UpdatedEvent(
  val oldValue: FirestoreValue,
  val value: FirestoreValue,
  val updatedFields: Set<String>
) : FirestoreEvent()

data class DeletedEvent(
  val resourceId: String
) : FirestoreEvent()

data class FirestoreValue(
  val resourceId: String,
  val createTime: Date,
  val updateTime: Date,
  val fields: Map<String, Any>
)

private fun RawFirestoreEvent.map(): FirestoreEvent {
  checkNotNull(oldValue)
  checkNotNull(value)
  return when {
    oldValue.name != null && value.name == null -> {
      val resourceId = oldValue.parseResourceId()
      check(!resourceId.isNullOrEmpty())
      DeletedEvent(resourceId)
    }
    oldValue.name == null && value.name != null -> {
      CreatedEvent(value.map())
    }
    else -> {
      checkNotNull(updateMask)
      checkNotNull(updateMask.fieldPaths)
      check(updateMask.fieldPaths.isNotEmpty())
      UpdatedEvent(oldValue.map(), value.map(), updateMask.fieldPaths)
    }
  }
}

private fun RawFirestoreValue.map(): FirestoreValue {
  checkNotNull(createTime)
  checkNotNull(updateTime)
  checkNotNull(name)
  checkNotNull(fields)

  val resourceId = parseResourceId()
  check(!resourceId.isNullOrEmpty())

  val resultMap = HashMap<String, Any>()
  val fieldsJsonObject = fields.asJsonObject
  fieldsJsonObject.keySet().forEach {
    val fieldValues = fieldsJsonObject.getAsJsonObject(it)
    if (fieldValues.has("stringValue")) {
      resultMap[it] = fieldValues["stringValue"].asString
    }
    // Complete other type if necessary
  }
  return FirestoreValue(resourceId, createTime, updateTime, resultMap)
}

private fun RawFirestoreValue.parseResourceId() = name?.substringAfterLast("/", Strings.EMPTY)

private val UpdatedEvent.hasUserNameChanged get() = updatedFields.contains("name")
