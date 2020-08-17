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
    evaluationDependsOn(":store:admin")
    api(project(":store:admin"))
    api(project(":payment:model"))


    api(Config.Libs.KotlinLogging.jdk)
    implementation(Config.Libs.Stripe.java)
}
