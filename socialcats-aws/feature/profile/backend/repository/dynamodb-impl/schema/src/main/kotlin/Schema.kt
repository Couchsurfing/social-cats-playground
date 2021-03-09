package com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb

public object Schema {
  public const val TABLE_NAME: String = "Users"
  public const val GSI1_INDEX_NAME: String = "Gsi1Index"

  public object SharedAttributes {
    public const val PARTITION_KEY: String = "PK"
    public const val SORT_KEY: String = "SK"
    public const val ITEM_TYPE: String = "ItemType"
  }

  public object UserItem {

    public const val TYPE: String = "USER"
    public const val KEY_PREFIX: String = "$TYPE#"

    public object Attributes {
      public const val USER_ID: String = "UserId"
      public const val CREATED_AT: String = "CreatedAt"
      public const val EMAIL: String = "Email"
      public const val EMAIL_VERIFIED: String = "EmailVerified"
      public const val NAME: String = "Name"
      public const val IMAGE_COUNT: String = "ImageCount"
      public const val AVATAR_IMAGE_ID: String = "AvatarImageId"
    }
  }

  public object ImageItem {
    public const val TYPE: String = "IMAGE"
    public const val KEY_PREFIX: String = "$TYPE#"
    public object Attributes {
      public const val IMAGE_ID: String = "ImageId"
      public const val USER_ID: String = "UserId"
      public const val CREATED_AT: String = "CreatedAt"
    }
  }

  public object DeviceItem {

    public const val TYPE: String = "DEVICE"
    public const val KEY_PREFIX: String = "$TYPE#"
    public object Attributes {
      public const val INSTANCE_ID: String = "InstanceId"
      public const val TOKEN: String = "Token"
      public const val CREATED_AT: String = "CreatedAt"
      public const val PROVIDER: String = "Provider"
      public const val PLATFORM: String = "Platform"
      public const val APP_VERSION_CODE: String = "AppVersion"
      public const val LANGUAGE_TAG: String = "LanguageTag"
    }
  }
}
