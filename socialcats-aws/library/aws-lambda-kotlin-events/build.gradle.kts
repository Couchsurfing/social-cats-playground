plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
}

version = "1.0-SNAPSHOT"

dependencies {
  api(kotlin("stdlib"))

  api("org.jetbrains.kotlinx:kotlinx-serialization-core:_")
  api("org.jetbrains.kotlinx:kotlinx-datetime:_")
  implementation("com.squareup.okio:okio:_")
}
