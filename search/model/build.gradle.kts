import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
}

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
                implementation(Config.Libs.Kotlin.Serialization.common)
            }
        }
        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                api(Config.Libs.Kotlin.jdk8)
                implementation(Config.Libs.Kotlin.Serialization.jdk)
            }
        }
        js().compilations["main"].defaultSourceSet {
            dependencies {
                api(Config.Libs.Kotlin.js)
                api(Config.Libs.Kotlin.Serialization.js)
            }
        }
    }
}
