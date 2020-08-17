rootProject.name = "social-cats-playground"

include(":analytics")
include(":bug-reporter")
include(":frontend:di")
include(":backend:ktor-api")
include(":backend:functions:auth-user-created")
include(":backend:functions:firestore-user-changed")
include(":backend:functions:play-billing-notification")
include(":payment:admin")
include(":payment:admin:test-util")
include(":payment:client")
include(":payment:model")
include(":payment:presenter")
include(":payment:ui-android")
include(":profile:ui-android")
include(":store:common")
include(":store:admin")
include(":store:admin:test-util")
include(":store:client")
include(":store:client:test-util")
include(":feature-flags")
include(":frontend:android")
include(":frontend:android:base")
//include(":frontend:web")
include(":aws-request-signing")
include(":elasticsearch-request-signing")
include(":auth:client")
include(":auth:client:test-util")
include(":auth:ui")
include(":session:client")
include(":session:client:test-util")
include(":main-presenter")
include(":connectivity")
include(":connectivity:test-util")
include(":cloud-messaging:client")
include(":cloud-messaging:android")
include(":account:presenter")
include(":account:ui-android")
include(":search:model")
include(":search:admin")
include(":search:admin:repository")
include(":search:admin:repository:test-util")
include(":search:client")
include(":search:client:test-util")
include(":search:presenter")
include(":search:ui-android")
include(":api:social-cats")
include(":presentation:presenter")
include(":presentation:binder")
include(":themes")
include(":kotlin-util")
include(":ui-android-util")
include(":cloud-tasks")
include(":test-settings")
include(":test-util")

pluginManagement {

    val kotlinVersion: String by settings
    val navigationVersion: String by settings

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "androidx.navigation.safeargs.kotlin" ->
                    useModule("androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion")
                "kotlin-android", "kotlin-kapt" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
                "com.google.android.gms.oss-licenses-plugin" ->
                    useModule("com.google.android.gms:oss-licenses-plugin:0.10.2")
                "com.google.cloud.tools.appengine" -> useModule("com.google.cloud.tools:appengine-gradle-plugin:2.3.0")
            }
        }
    }
    plugins {
        id("org.jetbrains.kotlin.jvm") version "$kotlinVersion"
        id("org.jetbrains.kotlin.js") version "$kotlinVersion"
        id("org.jetbrains.kotlin.kapt") version "$kotlinVersion"
        id("org.jetbrains.kotlin.multiplatform") version "$kotlinVersion"
        id("org.jetbrains.kotlin.plugin.serialization") version "$kotlinVersion"
        id("com.github.johnrengelman.shadow") version "6.0.0"
        id("com.github.ben-manes.versions") version "0.29.0"
        id("org.jlleitschuh.gradle.ktlint") version "9.3.0"
    }
}
