package com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb

import com.nicolasmilliard.socialcatsaws.profile.model.Device
import com.nicolasmilliard.socialcatsaws.profile.model.DeviceIdProvider
import com.nicolasmilliard.socialcatsaws.profile.model.SupportedPlatform
import kotlinx.datetime.Instant
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags

internal val DEVICES_TABLE_SCHEMA: TableSchema<DeviceItem> = TableSchema.builder(DeviceItem::class.java)
  .newItemSupplier { DeviceItem() }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.SharedAttributes.PARTITION_KEY)
      .getter(DeviceItem::partition_key)
      .setter { obj, v -> obj.partition_key = v }
      .tags(StaticAttributeTags.primaryPartitionKey())
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.SharedAttributes.SORT_KEY)
      .getter(DeviceItem::sort_key)
      .setter { obj, v -> obj.sort_key = v }
      .tags(StaticAttributeTags.primarySortKey())
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.SharedAttributes.ITEM_TYPE)
      .getter(DeviceItem::item_type)
      .setter { obj, v -> obj.item_type = v }
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.DeviceItem.Attributes.INSTANCE_ID)
      .getter(DeviceItem::instanceId)
      .setter { obj, v -> obj.instanceId = v }
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.DeviceItem.Attributes.CREATED_AT)
      .getter { item -> item.createdAt.toString() }
      .setter { obj, v -> obj.createdAt = Instant.parse(v) }
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.DeviceItem.Attributes.TOKEN)
      .getter(DeviceItem::token)
      .setter { obj, v -> obj.token = v }
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.DeviceItem.Attributes.PROVIDER)
      .getter(DeviceItem::source)
      .setter { obj, v -> obj.source = v }
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.DeviceItem.Attributes.PLATFORM)
      .getter(DeviceItem::platform)
      .setter { obj, v -> obj.platform = v }
  }
  .addAttribute(
    Int::class.java
  ) {
    it.name(Schema.DeviceItem.Attributes.APP_VERSION_CODE)
      .getter(DeviceItem::appVersionCode)
      .setter { obj, v -> obj.appVersionCode = v }
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.DeviceItem.Attributes.LANGUAGE_TAG)
      .getter(DeviceItem::languageTag)
      .setter { obj, v -> obj.languageTag = v }
  }
  .build()

internal data class DeviceItem(
  var userId: String? = null,
  var instanceId: String? = null,
  var createdAt: Instant? = null,
  var token: String? = null,
  var source: String? = null,
  var platform: String? = null,
  var appVersionCode: Int? = null,
  var languageTag: String? = null,
) {
  var partition_key
    get() = key(token)
    set(_) {
      // Ignore needed by enhanced client
    }

  var sort_key
    get() = UserItem.key(userId)
    set(_) {
      // Ignore needed by enhanced client
    }

  var item_type
    get() = Schema.DeviceItem.TYPE
    set(_) {
      // Ignore needed by enhanced client
    }

  companion object {
    fun key(id: String?): String {
      return "${Schema.DeviceItem.KEY_PREFIX}$id"
    }
  }
}

internal fun DeviceItem.toDevice() =
  Device(
    userId!!, instanceId!!, createdAt!!, token!!,
    DeviceIdProvider.valueOf(source!!), SupportedPlatform.valueOf(platform!!), appVersionCode!!, languageTag!!
  )

internal fun Device.toDeviceItem() =
  DeviceItem(userId, instanceId, createdAt, token, provider.toString(), platform.toString(), appVersionCode, languageTag)
