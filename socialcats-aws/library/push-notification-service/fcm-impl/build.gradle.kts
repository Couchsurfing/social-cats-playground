plugins {
  kotlin("jvm")
}

version = "1.0-SNAPSHOT"

dependencies {
  api(project(":library:push-notification-service"))
  api(kotlin("stdlib"))

  api("com.google.firebase:firebase-admin:_")

  implementation("io.github.microutils:kotlin-logging-jvm:_")
}
