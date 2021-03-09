package com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb

import com.nicolasmilliard.socialcatsaws.profile.model.Image
import kotlinx.datetime.Instant
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags

internal val IMAGES_TABLE_SCHEMA: TableSchema<ImageItem> = TableSchema.builder(ImageItem::class.java)
  .newItemSupplier { ImageItem() }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.SharedAttributes.PARTITION_KEY)
      .getter(ImageItem::partition_key)
      .setter { obj, v -> obj.partition_key = v }
      .tags(StaticAttributeTags.primaryPartitionKey())
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.SharedAttributes.SORT_KEY)
      .getter(ImageItem::sort_key)
      .setter { obj, v -> obj.sort_key = v }
      .tags(StaticAttributeTags.primarySortKey())
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.SharedAttributes.ITEM_TYPE)
      .getter(ImageItem::item_type)
      .setter { obj, v -> obj.item_type = v }
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.ImageItem.Attributes.IMAGE_ID)
      .getter(ImageItem::id)
      .setter { obj, v -> obj.id = v }
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.ImageItem.Attributes.USER_ID)
      .getter(ImageItem::userId)
      .setter { obj, v -> obj.userId = v }
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.ImageItem.Attributes.CREATED_AT)
      .getter { item -> item.createdAt.toString() }
      .setter { obj, v -> obj.createdAt = Instant.parse(v) }
  }
  .build()

internal data class ImageItem(
  var id: String? = null,
  var userId: String? = null,
  var createdAt: Instant? = null,
) {
  var partition_key
    get() = UserItem.key(userId)
    set(_) {
      // Ignore needed by enhanced client
    }

  var sort_key
    get() = "${Schema.ImageItem.KEY_PREFIX}$createdAt#$id"
    set(_) {
      // Ignore needed by enhanced client
    }

  var item_type
    get() = Schema.ImageItem.TYPE
    set(_) {
      // Ignore needed by enhanced client
    }
}

internal fun ImageItem.toImage() = Image(id!!, userId!!, createdAt!!)
internal fun Image.toImageItem() = ImageItem(id, userId, createdAt)
