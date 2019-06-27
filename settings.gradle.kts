rootProject.name = "social-cats-playground"

include(":backend:socialcats-search-api")
include(":backend:functions:firestore-user-elasticsearch")
include(":frontend:android")
include(":frontend:web")
include(":aws-request-signing")
include(":search-api")

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven {
      name = "Kotlin EAP (for kotlin-frontend-plugin)"
      url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
    }
  }
  resolutionStrategy {
    eachPlugin {
      when (requested.id.id) {
        "org.jetbrains.kotlin.frontend" -> useModule(Config.Plugins.kotlinFrontEnd)
        "kotlin-dce-js", "kotlin2js", "kotlin-android", "kotlin-kapt" -> useModule(Config.Plugins.kotlinGradle)
        "com.google.android.gms.oss-licenses-plugin" -> useModule(Config.Plugins.ossLicenses)
        "com.google.cloud.tools.appengine" -> useModule(Config.Plugins.appEngineGradlePlugin)
        "com.android.application" -> useModule(Config.Plugins.android)
        "androidx.navigation.safeargs.kotlin" -> useModule(Config.Plugins.navigation)
      }
    }
  }
  plugins {
    id("org.jetbrains.kotlin.jvm") version Config.kotlinVersion
    id("org.jetbrains.kotlin.kapt") version Config.kotlinVersion
    id("com.github.johnrengelman.shadow") version Config.Plugins.shadowVersion
    id("com.github.ben-manes.versions") version Config.Plugins.gradleVersions
  }
}
