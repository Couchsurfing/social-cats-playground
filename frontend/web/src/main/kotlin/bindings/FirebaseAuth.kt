@file:JsModule("firebase/app")
@file:JsQualifier("auth")

package firebase.auth

import kotlin.js.Promise

external class FirebaseAuth {
    fun onAuthStateChanged(
        next: (User?) -> Unit,
        error: ((Error) -> Unit)?,
        completed: () -> Unit
    ): () -> Unit

    fun signOut(): Promise<Unit>
}

external class User

external class GoogleAuthProvider {
    companion object {
        val PROVIDER_ID: String
    }
}

external class PhoneAuthProvider {
    companion object {
        val PROVIDER_ID: String
    }
}

external interface Error {
    val code: String
    val message: String
}
