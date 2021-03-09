import de.fayard.refreshVersions.bootstrapRefreshVersions

buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies.classpath("de.fayard.refreshVersions:refreshVersions:0.9.7")
}

bootstrapRefreshVersions()

rootProject.name = "Social Cats Aws"
include(":themes:android-ui")
include(":feature:api-common")
include(":feature:event-registry")
include(":feature:event-source")
include(":feature:event-source:sqs")
include(":feature:event-publisher")
include(":feature:event-publisher:event-bridge")
include(":feature:event-publisher:sqs")
include(":feature:android-wiring")
include(":feature:auth:android")
include(":feature:auth:backend:functions:cognito-confirmation-dynamo")
include(":feature:push-notifications:android-client")
include(":feature:push-notifications:api")
include(":feature:push-notifications:api:model")
include(":feature:push-notifications:backend:functions:create-device-dynamo")
include(":feature:push-notifications:backend:functions:process-image-notification")
include(":feature:push-notifications:backend:functions:send-notification")
include(":feature:push-notifications:backend:use-cases")
include(":feature:home:android-ui")
include(":feature:profile:backend:models")
include(":feature:profile:backend:use-cases")
include(":feature:profile:backend:repository")
include(":feature:profile:backend:repository:dynamodb-impl")
include(":feature:profile:backend:repository:dynamodb-impl:integration-test-util")
include(":feature:profile:backend:repository:dynamodb-impl:schema")
include(":feature:profile:backend:repository:dynamodb-wiring")
include(":feature:profile:backend:functions:dynamodb-stream")
include(":feature:profile:android-ui")
include(":feature:conversations:repository:admin")
include(":feature:image-processing:api")
include(":feature:image-processing:api:model")
include(":feature:image-processing:android")
include(":feature:image-processing:backend:use-cases")
include(":feature:image-processing:backend:functions:image-upload-url")
include(":feature:image-processing:backend:functions:image-upload-dynamo")
include(":frontend:android")
include(":backend:aws-cdk")

include(":library:sharp")
include(":library:di-scope")
include(":library:cloud-metrics")
include(":library:cloud-metrics:fake")
include(":library:image-object-store")
include(":library:object-store")
include(":library:object-store:s3-impl")
include(":library:object-store:s3-wiring")
include(":library:object-store:fake")
include(":library:object-store:fake-wiring")
include(":library:android-datastore")
include(":library:android-activity-result")
include(":library:android-text-resource")
include(":library:push-notification-service")
include(":library:push-notification-service:fcm-impl")
include(":library:push-notification-service:fcm-wiring")
