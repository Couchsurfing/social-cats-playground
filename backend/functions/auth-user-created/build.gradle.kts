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

val functionalTest: SourceSet by sourceSets.creating {
    compileClasspath += sourceSets.getByName("main").output
    runtimeClasspath += sourceSets.getByName("main").output
}

val functionalTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val functionalTestRuntimeOnly: Configuration by configurations.getting {
    extendsFrom(configurations.runtimeOnly.get())
}

dependencies {

    implementation(project(":store:admin"))

//    implementation(Config.Libs.Kotlin.jdk8)

    implementation(Config.Libs.KotlinLogging.jdk)
    implementation(Config.Libs.logBackClassic)
    implementation(Config.Libs.GoogleCloud.loggingLogback)

    implementation(Config.Libs.Moshi.core) {
        exclude(module = "kotlin-reflect")
    }
    implementation(Config.Libs.Moshi.adapters)
    kapt(Config.Libs.Moshi.codegen)

    compileOnly(Config.Libs.GoogleFunction.functionFrameworkApi)

    testImplementation(project(":store:admin:test-util"))
    testImplementation(Config.Libs.GoogleFunction.functionFrameworkApi)
    testImplementation(Config.Libs.Test.junit)
    testImplementation(Config.Libs.Test.truth)

    functionalTestImplementation(Config.Libs.Test.junit)
    functionalTestImplementation(Config.Libs.Test.truth)
    functionalTestImplementation(Config.Libs.GoogleFunction.functionFrameworkApi)
}

tasks {
    register("functionalTest", Test::class) {
        description = "Run functional tests"
        group = "verification"

        testClassesDirs = functionalTest.output.classesDirs
        classpath = functionalTest.runtimeClasspath
        maxParallelForks = 1

        environment("GOOGLE_APPLICATION_CREDENTIALS", "src/functionalTest/service-account.json")
    }
}
