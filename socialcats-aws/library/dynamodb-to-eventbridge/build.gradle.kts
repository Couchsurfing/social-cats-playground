plugins {
  kotlin("jvm")
}

version = "1.0-SNAPSHOT"

dependencies {
  api(project(":library:aws-lambda-kotlin-events"))

  api(kotlin("stdlib"))

  api(platform("software.amazon.awssdk:bom:_"))
  api("software.amazon.awssdk:dynamodb")
  api("software.amazon.awssdk:eventbridge")
  api("software.amazon.awssdk:sqs")
  api("org.jetbrains.kotlinx:kotlinx-serialization-json:_")

  implementation("org.apache.logging.log4j:log4j-api:_")
  implementation("org.apache.logging.log4j:log4j-core:_")
  implementation("io.github.microutils:kotlin-logging-jvm:_")
}
