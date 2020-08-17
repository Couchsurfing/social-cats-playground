import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.github.johnrengelman.shadow")
}

java {
    sourceCompatibility = Config.GoogleCloud.Functions.sourceCompatibility
    targetCompatibility = Config.GoogleCloud.Functions.targetCompatibility
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = Config.GoogleCloud.Functions.kotlinJvmTarget
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
}

tasks.assemble.dependsOn("shadowJar")

dependencies {
    implementation(project(":aws-request-signing"))
    implementation(project(":elasticsearch-request-signing"))
    implementation(project(":search:admin"))
    implementation(project(":payment:admin"))
    implementation(project(":store:common"))

//    implementation(Config.Libs.Kotlin.jdk8)
    implementation(Config.Libs.Firebase.admin)

    implementation(Config.Libs.KotlinLogging.jdk)
    implementation(Config.Libs.logBackClassic)
    implementation(Config.Libs.GoogleCloud.loggingLogback)
    // Elastic search High level client uses log4j
    implementation(Config.Libs.log4jToSlf4j)

    implementation(Config.Libs.Moshi.core) {
        exclude(module = "kotlin-reflect")
    }
    implementation(Config.Libs.Moshi.adapters)
    kapt(Config.Libs.Moshi.codegen)

    // Cloud function deps
    compileOnly(Config.Libs.GoogleFunction.functionFrameworkApi)

    testImplementation(Config.Libs.GoogleFunction.functionFrameworkApi)
    testImplementation(Config.Libs.Test.junit)
    testImplementation(Config.Libs.Test.truth)
    testImplementation(project(":payment:admin:test-util"))
    testImplementation(project(":store:admin:test-util"))
    testImplementation(project(":search:admin:repository:test-util"))
}
