plugins {
  kotlin("jvm")
}

version = "1.0-SNAPSHOT"

dependencies {
  api(project(":library:cloud-metrics"))
  api(kotlin("stdlib"))

  implementation("io.github.microutils:kotlin-logging-jvm:_")
}
