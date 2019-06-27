plugins {
  `java-library`
  id("com.github.ben-manes.versions")
}

java {
  // (4)
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  api(platform(Config.Libs.awsJavaSdk))
  api(Config.Libs.awsJavaSdkApacheClient)
  api(Config.Libs.awsJavaSdkApacheAuth)
}
