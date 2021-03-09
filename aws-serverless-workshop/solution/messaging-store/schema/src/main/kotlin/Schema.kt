package com.nicolasmilliard.serverlessworkshop

public object Schema {
    public const val TABLE_NAME: String = "Conversations"

    public object SharedAttributes {
        public const val PARTITION_KEY: String = "PK"
        public const val SORT_KEY: String = "SK"
        public const val ITEM_TYPE: String = "ItemType"
    }

    public object ConversationItem {

        public const val TYPE: String = "CONVERSATION"
        public const val KEY_PREFIX: String = "$TYPE#"

        public object Attributes {
            public const val CONVERSATION_ID: String = "ConversationId"
            public const val NAME: String = "Name"
        }
    }

    public object MessageItem {
        public const val TYPE: String = "MESSAGE"
        public const val KEY_PREFIX: String = "$TYPE#"
        public object Attributes {
            public const val MESSAGE_ID: String = "MessageId"
            public const val CREATED_AT: String = "CreatedAt"
            public const val CONTENT: String = "Content"
        }
    }
}
