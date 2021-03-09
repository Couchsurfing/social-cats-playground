plugins {
  kotlin("jvm")
}

version = "1.0-SNAPSHOT"

dependencies {

  api(project(":feature:event-publisher"))
  api(kotlin("stdlib"))

  api(platform("software.amazon.awssdk:bom:_"))
  api("software.amazon.awssdk:sqs")
  implementation("io.github.microutils:kotlin-logging-jvm:_")
}
