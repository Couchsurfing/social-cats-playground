import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

java {
    sourceCompatibility = Config.CloudCommon.sourceCompatibility
    targetCompatibility = Config.CloudCommon.targetCompatibility
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = Config.CloudCommon.kotlinJvmTarget
}

dependencies {

    api(Config.Libs.KotlinLogging.jdk)
    api(platform(Config.Libs.Aws.sdkBom))
    api(Config.Libs.Aws.apacheClient)
    api(Config.Libs.Aws.sdkAuth)
}
