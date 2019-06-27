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
    evaluationDependsOn(":search:admin")
    implementation(project(":search:admin"))

    implementation(Config.Libs.Kotlin.jdk8)
    implementation(Config.Libs.Firebase.admin)

    implementation(Config.Libs.KotlinLogging.js)
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
    testImplementation(Config.Libs.Test.truth)
}
