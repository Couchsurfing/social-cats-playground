import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.google.cloud.tools.appengine")
    id("com.github.ben-manes.versions")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

appengine {
    deploy {
        projectId = Config.GoogleCloud.projectId
        version = Config.Versions.SearchApi.version
    }
}

dependencies {
    implementation(project(":aws-request-signing"))
    evaluationDependsOn(":search:admin")
    implementation(project(":search:admin"))
    implementation(Config.Libs.Kotlin.jdk8)
    implementation(Config.Libs.Ktor.ktorServerNetty)
    implementation(Config.Libs.Ktor.ktorSerialization)
    implementation(Config.Libs.Ktor.ktorAuth)
    implementation(Config.Libs.Ktor.ktorLocation)
    implementation(Config.Libs.Kotlin.Serialization.jdk)
    implementation(Config.Libs.logBackClassic)
    // Elastic search High level client uses log4j
    implementation(Config.Libs.log4jToSlf4j)
    implementation(Config.Libs.Firebase.admin)

    testImplementation(Config.Libs.Ktor.ktorServerTest)
}

tasks {
    jar {
        manifest {
            attributes("Main-Class" to "com.nicolasmilliard.socialcats.searchapi.ApplicationKt")
        }
        from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }

    val copyYaml = register("initConfig", Copy::class) {
        from("src/main/appengine") {
            include("env_variables.yaml")
        }
        into("build/staged-app")
    }

    appengineStage {
        finalizedBy(copyYaml)
    }
}
