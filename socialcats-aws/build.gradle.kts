import javax.imageio.ImageIO

group = "com.nicolasmilliard.socialcats-aws"

buildscript {
  repositories {
    mavenCentral()
    google()
    jcenter()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:_")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
    classpath("app.cash.exhaustive:exhaustive-gradle:_")
    classpath("com.google.dagger:hilt-android-gradle-plugin:_")
  }
}

plugins {
  id("com.diffplug.spotless")
}

subprojects {

  apply(plugin = "com.diffplug.spotless")

  repositories {
    google()
    jcenter()
    mavenCentral()
  }

  spotless {
    kotlin {
      target("**/*.kt")
      ktlint("0.40.0").userData(mapOf("indent_size" to "2", "continuation_indent_size" to "2"))
    }
    kotlinGradle {
      ktlint("0.40.0").userData(mapOf("indent_size" to "2", "continuation_indent_size" to "2"))
    }
  }
}
