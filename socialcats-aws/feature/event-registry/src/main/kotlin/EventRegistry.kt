package com.nicolasmilliard.socialcatsaws.eventregistry

public object EventRegistry {

  public object EventSource {
    public const val UsersRepositoryFanout: String = "sc.users-repository-stream"
    public const val NewImageNotificationProcessing: String = "sc.new-image-notification-processing"
  }

  public object EventType {
    public object DynamodbStreamRecord {
      public const val EventNameInsert: String = "INSERT"
      public const val EventDetailTypeImageRecordList: String =
        "sc.dynamodb-stream-new-image-record-list"
    }

    public object DeviceNotification {
      public const val EventDetailType: String = "device-notification"
    }
  }
}
