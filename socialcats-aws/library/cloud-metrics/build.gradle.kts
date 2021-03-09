plugins {
  kotlin("jvm")
}

version = "1.0-SNAPSHOT"

dependencies {
  api(kotlin("stdlib"))

  implementation("software.amazon.cloudwatchlogs:aws-embedded-metrics:_")

  implementation("io.github.microutils:kotlin-logging-jvm:_")
}
