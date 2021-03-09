plugins {
  kotlin("jvm")
}

version = "1.0-SNAPSHOT"

dependencies {
  api(project(":feature:profile:backend:use-cases"))
  api(project(":library:cloud-metrics"))
  implementation(project(":library:image-object-store"))
  implementation(project(":feature:profile:backend:repository"))

  api(kotlin("stdlib"))

  implementation("io.github.microutils:kotlin-logging-jvm:_")
}
