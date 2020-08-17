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
    evaluationDependsOn(":store:common")
    api(project(":store:common"))
    evaluationDependsOn(":search:model")
    api(project(":search:model"))
    evaluationDependsOn(":search:admin:repository")
    api(project(":search:admin:repository"))

    api(Config.Libs.KotlinLogging.jdk)
}
