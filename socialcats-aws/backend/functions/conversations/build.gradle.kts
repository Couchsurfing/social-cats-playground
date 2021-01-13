import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("com.github.johnrengelman.shadow")
}

group = "com.nicolasmilliard.socialcatsaws.backend.functions"
version = "1.0-SNAPSHOT"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  sourceCompatibility = "11"
  targetCompatibility = "11"

  kotlinOptions {
    jvmTarget = "11"
    useIR = true
  }
}

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
  implementation(kotlin("stdlib"))
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")

  implementation(platform("software.amazon.awssdk:bom:2.16.3"))
  implementation("software.amazon.awssdk:lambda")
  implementation("com.amazonaws:aws-lambda-java-core:_")
  implementation("com.amazonaws:aws-lambda-java-events:_")

  implementation("org.apache.logging.log4j:log4j-api:_")
  implementation("org.apache.logging.log4j:log4j-core:_")
  implementation("io.github.microutils:kotlin-logging-jvm:_")

  implementation("app.cash.tempest:tempest2:_")
  implementation("software.amazon.awssdk:dynamodb-enhanced:_"){
    exclude(group = "software.amazon.awssdk", module = "http-client-spi")
    exclude(group = "software.amazon.awssdk", module = "apache-client")
    exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
  }

  implementation("org.jetbrains.kotlinx:kotlinx-datetime:_")

  runtimeOnly("org.apache.logging.log4j:log4j-slf4j18-impl:_")
  runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:_")
  testImplementation("org.junit.jupiter:junit-jupiter-api:_")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:_")
}
