@file:JsModule("react-firebaseui")

package firebaseui.auth

import firebase.auth.FirebaseAuth
import react.Component
import react.RProps
import react.RState
import react.ReactElement

external interface Props : RProps {
    var uiConfig: dynamic
    var uiCallback: ((AuthUI) -> Unit)?
    var firebaseAuth: FirebaseAuth
    var className: String?
}

@JsName("FirebaseAuth")
external class FirebaseAuthComponent : Component<Props, RState> {
    override fun render(): ReactElement?
}
