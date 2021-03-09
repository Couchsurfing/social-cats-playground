plugins {
  kotlin("jvm")
  kotlin("kapt")
  id("com.squareup.anvil")
}

version = "1.0-SNAPSHOT"

dependencies {
  api(project(":library:object-store:fake"))
  api(kotlin("stdlib"))

  implementation("com.google.dagger:dagger:_")
  kapt("com.google.dagger:dagger-compiler:_")

  implementation("io.github.microutils:kotlin-logging-jvm:_")
}
