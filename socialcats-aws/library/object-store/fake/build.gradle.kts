plugins {
  kotlin("jvm")
}

version = "1.0-SNAPSHOT"

dependencies {
  api(project(":library:object-store"))
  api(kotlin("stdlib"))

  implementation("io.github.microutils:kotlin-logging-jvm:_")
}
