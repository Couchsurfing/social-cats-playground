plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
}

kotlin {
  jvm {
    val main by compilations.getting {
      java.sourceCompatibility = Config.Java.sourceCompatibility
      java.targetCompatibility = Config.Java.targetCompatibility
      kotlinOptions {
        jvmTarget = Config.Kotlin.jvmTarget
      }
    }
  }
  sourceSets {
    commonMain {
      dependencies {
        api("org.jetbrains.kotlinx:kotlinx-serialization-core:_")
      }
    }
  }
}
