plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

dependencies {
    implementation(project(":session:client"))
    implementation(project(":connectivity"))
    implementation(project(":search:presenter"))
    implementation(project(":frontend:android:base"))
    implementation(project(":ui-android-util"))
    implementation(project(":presentation:binder"))
    implementation(project(":themes"))
    implementation(project(":profile:ui-android"))

//    implementation(Config.Libs.Kotlin.jdk8)
    implementation(Config.Libs.Kotlin.Coroutine.android)

    implementation(Config.Libs.AndroidX.fragmentKtx)
    implementation(Config.Libs.AndroidX.lifecycleKtx)
    implementation(Config.Libs.AndroidX.coreKtx)
    implementation(Config.Libs.AndroidX.constraintLayout)
    implementation(Config.Libs.AndroidX.recyclerView)

    implementation(Config.Libs.material)
    implementation(Config.Libs.coil)
    implementation(Config.Libs.timber)
}
