plugins {
  kotlin("jvm")
}

version = "1.0-SNAPSHOT"

dependencies {
  implementation(project(":feature:profile:backend:repository:dynamodb-impl:schema"))
  api(project(":feature:profile:backend:repository:dynamodb-impl"))
  implementation(project(":library:cloud-metrics:fake"))
  api(kotlin("stdlib"))

  api(platform("software.amazon.awssdk:bom:_"))
  api("software.amazon.awssdk:dynamodb-enhanced") {
    exclude(group = "software.amazon.awssdk", module = "http-client-spi")
    exclude(group = "software.amazon.awssdk", module = "apache-client")
    exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
  }

  implementation("io.github.microutils:kotlin-logging-jvm:_")
}
