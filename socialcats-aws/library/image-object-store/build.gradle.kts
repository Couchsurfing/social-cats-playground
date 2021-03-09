plugins {
  kotlin("jvm")
}

version = "1.0-SNAPSHOT"

dependencies {
  implementation(project(":library:object-store"))
  api(kotlin("stdlib"))

  api("javax.inject:javax.inject:_")

  implementation("io.github.microutils:kotlin-logging-jvm:_")
}
