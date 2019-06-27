import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
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

dependencies {
    api(project(":search:presenterr"))
    api(project(":presentation:binder"))

    implementation(Config.Libs.Kotlin.jdk8)
    implementation(Config.Libs.Kotlin.Coroutine.android)

    implementation(Config.Libs.AndroidX.coreKtx)
    implementation(Config.Libs.AndroidX.constraintLayout)
    implementation(Config.Libs.AndroidX.recyclerView)

    implementation(Config.Libs.material)
}
