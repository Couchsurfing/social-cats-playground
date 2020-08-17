plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

dependencies {

//    implementation(Config.Libs.Kotlin.jdk8)
    implementation(Config.Libs.Kotlin.Coroutine.android)

    implementation(Config.Libs.AndroidX.fragmentKtx)
    implementation(Config.Libs.AndroidX.lifecycleKtx)
    implementation(Config.Libs.AndroidX.lifecycleCommon)
    implementation(Config.Libs.AndroidX.coreKtx)

    implementation(Config.Libs.material)
    implementation(Config.Libs.timber)
}
