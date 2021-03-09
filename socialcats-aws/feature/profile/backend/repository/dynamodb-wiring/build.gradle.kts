plugins {
  kotlin("jvm")
  kotlin("kapt")
  id("com.squareup.anvil")
}

version = "1.0-SNAPSHOT"

dependencies {
  api(project(":library:di-scope"))
  api(project(":feature:profile:backend:repository:dynamodb-impl"))
  api(kotlin("stdlib"))

  implementation("com.google.dagger:dagger:_")
  kapt("com.google.dagger:dagger-compiler:_")

  api("org.jetbrains.kotlinx:kotlinx-datetime:_")

  implementation("io.github.microutils:kotlin-logging-jvm:_")
}
