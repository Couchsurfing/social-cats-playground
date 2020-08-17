plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm {
        val main by compilations.getting {
            java.sourceCompatibility = Config.Common.sourceCompatibility
            java.targetCompatibility = Config.Common.targetCompatibility
            kotlinOptions {
                jvmTarget = Config.Common.kotlinJvmTarget
            }
        }
    }
    js {
        browser()
        compilations["main"].kotlinOptions {
            freeCompilerArgs += listOf(
                "-Xuse-experimental=kotlinx.serialization.UnstableDefault"
            )
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                api(Config.Libs.Kotlin.Coroutine.core)
                implementation(Config.Libs.Kotlin.Serialization.core)
            }
        }
        val jvmMain by getting {
            dependencies {
                api(Config.Libs.Retrofit.client)
                api(Config.Libs.OkHttp.client)
                api(Config.Libs.Retrofit.converterKotlinxSerialization) {
                    exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-serialization-runtime")
                }
            }
        }
    }
}
