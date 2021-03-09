plugins {
  kotlin("jvm")
}

version = "1.0-SNAPSHOT"

dependencies {
  api(project(":feature:image-processing:api:model"))
  api(kotlin("stdlib"))
  api("com.squareup.retrofit2:retrofit:_")
}
