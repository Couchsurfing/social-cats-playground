buildscript {
    extra.apply {
        set("isCiBuild", System.getenv("CI") == "true")
    }

    dependencies {
        classpath(Config.Plugins.googleServices)
        classpath(Config.Plugins.ktlint)
    }
}

plugins {
    kotlin("jvm") apply false
    id("androidx.navigation.safeargs.kotlin") apply false
    id("com.google.android.gms.oss-licenses-plugin") apply false
    id("org.jetbrains.kotlin.frontend") apply false
    id("com.github.ben-manes.versions")
}

allprojects {
    group = "com.nicolasmilliard.socialcats"
    version = Config.Versions.name

    repositories {
        maven("https://kotlin.bintray.com/ktor")
        maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
        google()
        mavenCentral()
        jcenter()
    }

    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinCompile::class).configureEach {
        kotlinOptions {
            freeCompilerArgs += listOf(
                "-progressive",
                "-Xnew-inference",
                "-Xuse-experimental=kotlinx.serialization.UnstableDefault",
                "-Xuse-experimental=io.ktor.locations.KtorExperimentalLocationsAPI",
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
        }
    }

//    ktlint {
//        version.set("0.34.2")
//    }
}
