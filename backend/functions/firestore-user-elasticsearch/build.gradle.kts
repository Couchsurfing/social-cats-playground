import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  id("com.github.johnrengelman.shadow")
  id("com.github.ben-manes.versions")
}

// tasks.getByName("build").dependsOn( tasks.getByName("shadowJar"))

tasks.withType<ShadowJar> {
  mergeServiceFiles()
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

dependencies {
  implementation(project(":aws-request-signing"))
  implementation(project(":search-api"))

  implementation(kotlin("stdlib-jdk8", Config.kotlinVersion))
  implementation(Config.Libs.firebaseAdmin)

  implementation(Config.Libs.kotlinLogging)
  implementation(Config.Libs.logBackClassic)
  // Elastic search High level client uses log4j
  implementation(Config.Libs.log4jToSlf4j)

  // Cloud function deps
  implementation(Config.Libs.GoogleFunction.gson)

  compileOnly(Config.Libs.GoogleFunction.javaServletApi)
  compileOnly(Config.Libs.GoogleFunction.functionFrameworkApi)

  testImplementation(Config.Libs.GoogleFunction.javaServletApi)
  testImplementation(Config.Libs.GoogleFunction.functionFrameworkApi)
  testImplementation(Config.Libs.GoogleFunction.junit)
  testImplementation(Config.Libs.GoogleFunction.mockito)
  testImplementation(Config.Libs.GoogleFunction.truth)
}
