package com.myorg

import software.amazon.awscdk.core.App

fun main() {
    val app = App()
    AwsCdkStack(app, "AwsCdkStackDev", null, false)
//    AwsCdkStack(app, "AwsCdkStackProd", null, true)
    app.synth()
}
