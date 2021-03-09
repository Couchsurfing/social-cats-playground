import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":messaging-store"))
    implementation(kotlin("stdlib"))

    implementation(platform("software.amazon.awssdk:bom:2.16.16"))
    implementation("software.amazon.awssdk:lambda")
    implementation("software.amazon.awssdk:url-connection-client")

    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation("com.amazonaws:aws-lambda-java-events:3.7.0")

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.6")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")


    runtimeOnly("org.apache.logging.log4j:log4j-slf4j18-impl:2.14.0")
    runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")

    testImplementation("com.amazonaws:aws-lambda-java-tests:1.0.0")
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
    transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer())
    exclude("META-INF/maven/**")
    exclude("META-INF/proguard/**")
    exclude("META-INF/**/module-info.class")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}
