import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {

    api(project(":analytics"))
    implementation(project(":frontend:di"))
    implementation(project(":bug-reporter"))
    implementation(project(":cloud-messaging:android"))
    implementation(project(":presentation:binder"))
    implementation(project(":main-presenter"))
    implementation(project(":themes"))


    api(Config.Libs.Kotlin.Coroutine.android)

    implementation(Config.Libs.Play.core)

    implementation(Config.Libs.AndroidX.appCompat)
    implementation(Config.Libs.AndroidX.activityKtx)
    implementation(Config.Libs.AndroidX.fragmentKtx)
    implementation(Config.Libs.AndroidX.preferenceKtx)
    implementation(Config.Libs.AndroidX.coreKtx)
    implementation(Config.Libs.AndroidX.constraintLayout)
    implementation(Config.Libs.AndroidX.vectorCompat)
    api(Config.Libs.AndroidX.navigationFragmentKtx)
    api(Config.Libs.AndroidX.navigationUiKtx)
    implementation(Config.Libs.AndroidX.viewModelKtx)
    implementation(Config.Libs.AndroidX.lifecycleKtx)
    implementation(Config.Libs.AndroidX.lifecycleCommon)

    implementation(Config.Libs.Firebase.common)
    implementation(Config.Libs.Firebase.performance)
    implementation(Config.Libs.Firebase.inAppMessaging)

    implementation(Config.Libs.OkHttp.client)
    implementation(Config.Libs.OkHttp.logging)

    implementation(Config.Libs.byteUnits)
    implementation(Config.Libs.slf4jTimber)
    implementation(Config.Libs.timber)

    implementation(Config.Libs.coil)
    api(Config.Libs.Koin.androidxViewModel)
}
