package firebaseui.auth

import firebase.auth.FirebaseAuth
import react.RBuilder

fun RBuilder.firebaseAuthComponent(
    uiConfig: dynamic,
    uiCallback: ((AuthUI) -> Unit)? = null,
    firebaseAuth: FirebaseAuth,
    className: String? = null
) = child(FirebaseAuthComponent::class) {
    attrs.uiConfig = uiConfig
    attrs.firebaseAuth = firebaseAuth
    attrs.uiCallback = uiCallback
    attrs.className = className
}
