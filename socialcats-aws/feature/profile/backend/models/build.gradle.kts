plugins {
  kotlin("jvm")
}

version = "1.0-SNAPSHOT"

dependencies {

  api(kotlin("stdlib"))

  api("org.jetbrains.kotlinx:kotlinx-datetime:_")

  implementation("io.github.microutils:kotlin-logging-jvm:_")
}
