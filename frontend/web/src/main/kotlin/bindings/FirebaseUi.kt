@file:JsModule("firebaseui")
@file:JsQualifier("auth")

package firebaseui.auth

import kotlin.js.Promise

external class AuthUI(auth: dynamic) {
    fun start(container: String, config: dynamic)
    fun disableAutoSignIn()
    fun signIn()
    fun reset()
    fun delete(): Promise<Unit>
    fun isPendingRedirect(): Boolean
}

external interface Callbacks {
    fun signInSuccessWithAuthResult(
        authResult: dynamic,
        redirectUrl: String?
    ): Boolean

    fun signInFailure(error: AuthUIError): Promise<Unit>
    fun uiShown()
}

external class AuthUIError {
    val code: String
    val message: String
    val credential: dynamic
    fun toJSON(): dynamic
}
