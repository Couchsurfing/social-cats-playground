import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("com.google.cloud.tools.appengine")
}

java {
    sourceCompatibility = Config.GoogleCloud.AppEngine.sourceCompatibility
    targetCompatibility = Config.GoogleCloud.AppEngine.targetCompatibility
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = Config.GoogleCloud.AppEngine.kotlinJvmTarget
        freeCompilerArgs += listOf(
            "-Xuse-experimental=io.ktor.util.KtorExperimentalAPI",
            "-Xuse-experimental=io.ktor.locations.KtorExperimentalLocationsAPI",
            "-Xuse-experimental=kotlinx.serialization.UnstableDefault"
        )
    }
}

val functionalTest: SourceSet by sourceSets.creating

val functionalTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val functionalTestRuntimeOnly: Configuration by configurations.getting {
    extendsFrom(configurations.runtimeOnly.get())
}

appengine {
    deploy {
        projectId = Config.GoogleCloud.projectId
        version = Config.SearchApi.version
    }
}

dependencies {
    implementation(project(":aws-request-signing"))
    implementation(project(":elasticsearch-request-signing"))
    evaluationDependsOn(":search:admin")
    implementation(project(":search:admin"))
    evaluationDependsOn(":payment:admin")
    implementation(project(":payment:admin"))
    evaluationDependsOn(":payment:model")
    implementation(project(":payment:model"))
    implementation(project(":api:social-cats"))
//    implementation(Config.Libs.Kotlin.jdk8)
    implementation(Config.Libs.Ktor.ktorServerNetty)
    implementation(Config.Libs.Ktor.ktorSerialization)
    implementation(Config.Libs.Ktor.ktorAuth)
    implementation(Config.Libs.Ktor.ktorLocation)
    implementation(Config.Libs.Koin.ktor)
    implementation(Config.Libs.Kotlin.Serialization.core)
    implementation(Config.Libs.logBackClassic)
    // Elastic search High level client uses log4j
    implementation(Config.Libs.log4jToSlf4j)
    implementation(Config.Libs.GoogleCloud.loggingLogback)
    implementation(Config.Libs.Firebase.admin)

    evaluationDependsOn(":search:admin:repository:test-util")
    testImplementation(project(":search:admin:repository:test-util"))
    evaluationDependsOn(":payment:admin:test-util")
    testImplementation(project(":payment:admin:test-util"))
    evaluationDependsOn(":store:admin:test-util")
    testImplementation(project(":store:admin:test-util"))
    testImplementation(Config.Libs.Ktor.ktorServerTestHost)
    testImplementation(Config.Libs.Test.truth)
    testImplementation(Config.Libs.Koin.test)
//    testImplementation(Config.Libs.OkHttp.client)

    functionalTestImplementation(Config.Libs.Test.junit)
    functionalTestImplementation(Config.Libs.Test.truth)
    functionalTestImplementation(Config.Libs.OkHttp.client)
}

tasks {

    jar {
        manifest {
            attributes("Main-Class" to "com.nicolasmilliard.socialcats.searchapi.ApplicationKt")
        }
        enabled = false
    }

    shadowJar {
        archiveFileName.set("ktor-api-0.1.0-SNAPSHOT.jar")
        mergeServiceFiles()
    }

    assemble.dependsOn("shadowJar")

    val copyYaml = register("initConfig", Copy::class) {
        from("src/main/appengine") {
            include("env_variables.yaml")
        }
        into("build/staged-app")
    }

    appengineStage {
        finalizedBy(copyYaml)
        artifacts {
            add("archives", shadowJar)
        }
    }

    register("functionalTest", Test::class) {
        description = "Run functional tests"
        group = "verification"

        testClassesDirs = functionalTest.output.classesDirs
        classpath = functionalTest.runtimeClasspath
        maxParallelForks = 1
    }
}
