plugins {
  kotlin("jvm")
}

group = "com.nicolasmilliard.socialcatsaws.backend.repository"
version = "1.0-SNAPSHOT"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  sourceCompatibility = "11"
  targetCompatibility = "11"

  kotlinOptions {
    jvmTarget = "11"
    useIR = true
    freeCompilerArgs += "-Xexplicit-api=strict"
    freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
  }
}

dependencies {
  implementation(project(":feature:profile:backend:models"))
  implementation(project(":library:cloud-metrics"))
  api(kotlin("stdlib"))

  implementation(platform("software.amazon.awssdk:bom:2.16.3"))
  implementation("software.amazon.awssdk:dynamodb-enhanced:_") {
    exclude(group = "software.amazon.awssdk", module = "http-client-spi")
    exclude(group = "software.amazon.awssdk", module = "apache-client")
    exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
  }
  implementation("software.amazon.awssdk:url-connection-client")


  api("org.jetbrains.kotlinx:kotlinx-datetime:_")

  implementation("org.apache.logging.log4j:log4j-api:_")
  implementation("org.apache.logging.log4j:log4j-core:_")
  implementation("io.github.microutils:kotlin-logging-jvm:_")
}
