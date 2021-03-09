import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  kotlin("jvm")
  kotlin("kapt")
  id("com.github.johnrengelman.shadow")
  id("com.squareup.anvil")
}

version = "1.0-SNAPSHOT"

tasks.withType<ShadowJar> {
  mergeServiceFiles()
  transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer())
  exclude("META-INF/maven/**")
  exclude("META-INF/proguard/**")
  exclude("META-INF/**/module-info.class")
}

tasks.register<proguard.gradle.ProGuardTask>("proguard") {
  configuration("proguard.cfg")
  injars("$buildDir/libs/${project.name}-$version-all.jar")
  outjars("$buildDir/libs/${project.name}-$version-proguard.jar")
  libraryjars("${System.getProperty("java.home")}/jmods")
}

tasks["proguard"].dependsOn(tasks["shadowJar"])

val proguard by configurations.creating {
}

//
artifacts {
  add("proguard", file("$buildDir/libs/${project.name}-$version-proguard.jar")) {
    builtBy(tasks["proguard"])
  }
  add("shadow", tasks["shadowJar"]) {
    builtBy(tasks["shadowJar"])
  }
}

dependencies {
  implementation(project(":library:cloud-metrics"))
  implementation(project(":feature:profile:backend:repository:dynamodb-wiring"))
  implementation(project(":library:push-notification-service"))
  implementation(project(":feature:event-registry"))
  implementation(project(":feature:event-publisher:sqs")) {
    exclude(group = "software.amazon.awssdk", module = "http-client-spi")
    exclude(group = "software.amazon.awssdk", module = "apache-client")
    exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
  }

  implementation(kotlin("stdlib"))

  implementation("com.google.dagger:dagger:_")
  kapt("com.google.dagger:dagger-compiler:_")

  implementation(platform("software.amazon.awssdk:bom:_"))
  implementation("software.amazon.awssdk:lambda")

  implementation("com.amazonaws:aws-lambda-java-core:_")
  implementation("com.amazonaws:aws-lambda-java-events:_")

  implementation("software.amazon.awssdk:url-connection-client")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:_")

  implementation("org.apache.logging.log4j:log4j-api:_")
  implementation("org.apache.logging.log4j:log4j-core:_")
  implementation("io.github.microutils:kotlin-logging-jvm:_")

  runtimeOnly("org.apache.logging.log4j:log4j-slf4j18-impl:_")
  runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:_")
  testImplementation("org.junit.jupiter:junit-jupiter-api:_")
  testImplementation("org.junit.jupiter:junit-jupiter-params:_")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:_")

  testImplementation(project(":library:cloud-metrics:fake"))
  testImplementation(project(":feature:profile:backend:repository:dynamodb-impl:integration-test-util"))

  testImplementation("com.amazonaws:aws-java-sdk-core:_")
  testImplementation("org.apache.logging.log4j:log4j-slf4j18-impl:_")

  testImplementation("org.testcontainers:testcontainers:_")
  testImplementation("org.testcontainers:junit-jupiter:_")
  testImplementation("org.testcontainers:localstack:_")


  kaptTest("com.google.dagger:dagger-compiler:_")
}

kapt {
  correctErrorTypes = true
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    events("passed", "skipped", "failed")
  }
}
