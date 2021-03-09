plugins {
  kotlin("jvm")
}

version = "1.0-SNAPSHOT"

dependencies {
  api(kotlin("stdlib"))
  api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:_")

  implementation(platform("software.amazon.awssdk:bom:_"))
  implementation("software.amazon.awssdk:dynamodb-enhanced") {
    exclude(group = "software.amazon.awssdk", module = "http-client-spi")
    exclude(group = "software.amazon.awssdk", module = "apache-client")
    exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
  }

  api("org.jetbrains.kotlinx:kotlinx-datetime:_")

  implementation("io.github.microutils:kotlin-logging-jvm:_")
}
