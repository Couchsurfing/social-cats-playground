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
                api(Config.Libs.Kotlin.Coroutine.common)
                api(project(":search:model"))
                implementation(Config.Libs.Kotlin.Serialization.common)
            }
        }
        val jvmMain by getting {
            dependencies {
                api(Config.Libs.Kotlin.jdk8)
                api(Config.Libs.Kotlin.Coroutine.jdk8)
                implementation(Config.Libs.Kotlin.Serialization.jdk)
                implementation(Config.Libs.Retrofit.client)
                implementation(Config.Libs.Retrofit.converterKotlinxSerialization)
                api(Config.Libs.OkHttp.client)
                api(Config.Libs.okIo)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation(Config.Libs.Test.truth)
            }
        }
        val jsMain by getting {
            dependencies {
                api(Config.Libs.Kotlin.js)
                api(Config.Libs.Kotlin.Coroutine.js)
                api(Config.Libs.Kotlin.Serialization.js)
            }
        }
    }
}
