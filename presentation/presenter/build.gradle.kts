import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
}

base.archivesBaseName = "presentation-presenter"

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

kotlin {
    jvm()
    js().compilations["main"].kotlinOptions {
        moduleKind = "umd"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(Config.Libs.Kotlin.common)
                api(Config.Libs.Kotlin.Coroutine.common)
            }
        }
        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                api(Config.Libs.Kotlin.jdk8)
                api(Config.Libs.Kotlin.Coroutine.jdk8)
            }
        }

        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation(Config.Libs.Test.truth)
            }
        }
        js().compilations["main"].defaultSourceSet {
            dependencies {
                api(Config.Libs.Kotlin.js)
                api(Config.Libs.Kotlin.Coroutine.js)
            }
        }
    }
}
