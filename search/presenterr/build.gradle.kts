import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

base.archivesBaseName = "search-presenter"

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

// TODO work around https://youtrack.jetbrains.com/issue/KT-27170
//configurations.create("compileClasspath")

kotlin {
    android()
    js().compilations["main"].kotlinOptions {
        moduleKind = "umd"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(project(":search:client"))
                api(project(":presentation:presenter"))
                api(Config.Libs.Kotlin.common)
                api(Config.Libs.Kotlin.Coroutine.common)
                implementation(Config.Libs.KotlinLogging.common)
            }
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                api(Config.Libs.Kotlin.jdk8)
                api(Config.Libs.Kotlin.Coroutine.jdk8)
                api(Config.Libs.KotlinLogging.jdk)
            }
        }

        js().compilations["main"].defaultSourceSet {
            dependencies {
                api(Config.Libs.Kotlin.js)
                api(Config.Libs.Kotlin.Coroutine.js)
                api(Config.Libs.KotlinLogging.js)
            }
        }
    }
}


android {
    compileSdkVersion(Config.Android.SdkVersions.compile)

    buildToolsVersion = Config.Android.buildToolsVersion

    defaultConfig {
        minSdkVersion(Config.Android.SdkVersions.min)
    }

    lintOptions {
        textReport = true
        textOutput("stdout")
        setLintConfig(rootProject.file("lint.xml"))
        isCheckReleaseBuilds = false
    }
}

