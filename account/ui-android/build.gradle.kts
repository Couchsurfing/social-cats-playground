plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    api(project(":frontend:android:base"))
    api(project(":presentation:binder"))
    api(project(":auth:ui"))
    api(project(":account:presenter"))

//    implementation(Config.Libs.Kotlin.jdk8)
    implementation(Config.Libs.Kotlin.Coroutine.android)

    implementation(Config.Libs.AndroidX.fragmentKtx)
    implementation(Config.Libs.AndroidX.lifecycleKtx)
    implementation(Config.Libs.AndroidX.coreKtx)
    implementation(Config.Libs.AndroidX.constraintLayout)
    implementation(Config.Libs.AndroidX.recyclerView)
    implementation(Config.Libs.Play.ossLicenses)

    implementation(Config.Libs.material)
}
