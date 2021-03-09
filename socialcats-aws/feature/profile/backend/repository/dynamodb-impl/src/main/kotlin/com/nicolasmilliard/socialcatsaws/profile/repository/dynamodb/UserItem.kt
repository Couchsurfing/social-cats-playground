package com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb

import com.nicolasmilliard.socialcatsaws.profile.model.Avatar
import com.nicolasmilliard.socialcatsaws.profile.model.User
import kotlinx.datetime.Instant
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags
import software.amazon.awssdk.enhanced.dynamodb.mapper.UpdateBehavior

internal val AVATAR_TABLE_SCHEMA: TableSchema<AvatarItem> = TableSchema.builder(AvatarItem::class.java)
  .newItemSupplier { AvatarItem() }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.UserItem.Attributes.AVATAR_IMAGE_ID)
      .getter(AvatarItem::avatar_image_id)
      .setter { obj, v -> obj.avatar_image_id = v }
  }
  .build()

internal val USERS_TABLE_SCHEMA: TableSchema<UserItem> = TableSchema.builder(UserItem::class.java)
  .newItemSupplier { UserItem() }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.SharedAttributes.PARTITION_KEY)
      .getter(UserItem::partition_key)
      .setter { obj, v -> obj.partition_key = v }
      .tags(StaticAttributeTags.primaryPartitionKey())
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.SharedAttributes.SORT_KEY)
      .getter(UserItem::sort_key)
      .setter { obj, v -> obj.sort_key = v }
      .tags(StaticAttributeTags.primarySortKey())
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.SharedAttributes.ITEM_TYPE)
      .getter(UserItem::item_type)
      .setter { obj, v -> obj.item_type = v }
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.UserItem.Attributes.USER_ID)
      .getter(UserItem::id)
      .setter { obj, v -> obj.id = v }
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.UserItem.Attributes.CREATED_AT)
      .getter { item -> item.createdAt.toString() }
      .setter { obj, v -> obj.createdAt = Instant.parse(v) }
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.UserItem.Attributes.EMAIL)
      .getter(UserItem::email)
      .setter { obj, v -> obj.email = v }
  }
  .addAttribute(
    Boolean::class.java
  ) {
    it.name(Schema.UserItem.Attributes.EMAIL_VERIFIED)
      .getter(UserItem::email_verified)
      .setter { obj, v -> obj.email_verified = v }
  }
  .addAttribute(
    String::class.java
  ) {
    it.name(Schema.UserItem.Attributes.NAME)
      .getter(UserItem::name)
      .setter { obj, v -> obj.name = v }
  }
  .flatten(AVATAR_TABLE_SCHEMA, UserItem::avatar) { obj, v -> obj.avatar = v }
  .addAttribute(
    Int::class.java
  ) {
    it.name(Schema.UserItem.Attributes.IMAGE_COUNT)
      .getter(UserItem::image_count)
      .setter { obj, v -> obj.image_count = v }
      .tags(StaticAttributeTags.updateBehavior(UpdateBehavior.WRITE_IF_NOT_EXISTS))
  }
  .build()

internal data class UserItem(
  var id: String? = null,
  var createdAt: Instant? = null,
  var email: String? = null,
  var email_verified: Boolean? = null,
  var name: String? = null,
  var avatar: AvatarItem? = null,
  var image_count: Int? = null,
  var notificationKey: String? = null,
) {
  var partition_key
    get() = key(id)
    set(_) {
      // Ignore needed by enhanced client
    }

  var sort_key
    get() = key(id)
    set(_) {
      // Ignore needed by enhanced client
    }

  var item_type
    get() = Schema.UserItem.TYPE
    set(_) {
      // Ignore needed by enhanced client
    }

  companion object {
    fun key(id: String?): String {
      return "${Schema.UserItem.KEY_PREFIX}$id"
    }
  }
}

internal data class AvatarItem(
  var avatar_image_id: String? = null,
)

internal fun AvatarItem.toAvatar() = Avatar(avatar_image_id!!)
internal fun Avatar.toAvatarItem() = AvatarItem(imageId)

internal fun UserItem.toUser() =
  User(id!!, createdAt!!, email!!, email_verified!!, name, avatar?.toAvatar(), image_count!!)

internal fun User.toUserItem() =
  UserItem(id, createdAt, email, emailVerified, name, avatar?.toAvatarItem(), imageCount)
