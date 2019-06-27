plugins {
  kotlin("jvm")
  kotlin("kapt")
  id("com.github.ben-manes.versions")
}

java {
  // (4)
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  api(kotlin("stdlib-jdk8", Config.kotlinVersion))

  api(Config.Libs.kotlinLogging)

  api(Config.Libs.elasticSearchHighLevelClient)

  kapt(Config.Libs.Moshi.moshiKotlinCodeGen)
  api(Config.Libs.Moshi.moshiAdapter)
}
