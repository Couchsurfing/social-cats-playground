package components

import firebase.auth.FirebaseAuth
import firebaseui.auth.AuthUIError
import firebaseui.auth.Callbacks
import firebaseui.auth.firebaseAuthComponent
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import mu.KotlinLogging
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.*
import react.router.dom.navLink
import react.setState

import views.topBar
import kotlin.js.Promise

private val logger = KotlinLogging.logger {}

interface HomeProps : RProps {
    var firebaseAuth: FirebaseAuth
}

class HomeComponent(props: HomeProps) : RComponent<HomeProps, HomeState>() {

    private val firebaseAuth = props.firebaseAuth

    override fun HomeState.init() {
        isSignedIn = false
        isAuthUiVisible = false
    }

    lateinit var authObserver: () -> Unit
    override fun componentDidMount() {
        authObserver = firebaseAuth.onAuthStateChanged({ user ->
            if (user == null) {
                logger.info { "Auth state: null" }
            } else {
                logger.info { "Auth state: $user" }
            }
            if (user != null) {
                setState {
                    isSignedIn = true
                    isAuthUiVisible = false
                }
            } else {
                setState { isSignedIn = false }
            }
        }, { error ->
            logger.error { "onAuthStateChanged Error: ${error.code}, ${error.message}" }
        }, {})
    }

    override fun componentWillUnmount() {
        authObserver()
    }

    override fun RBuilder.render() {
        topBar()
        div(classes = "main") {
            section {

                h2(classes = "mdc-typography--headline2") {
                    +"Social Cats"
                }

                article {
                    p(classes = "mdc-typography--body1") { +"An article" }
                    navLink("/terms", className = "mdc-button  mdc-button--raised") {
                        span(classes = "mdc-button__label") { +"Terms of Use" }
                    }
                    if (state.isSignedIn) {
                        button(classes = "mdc-button  mdc-button--raised") {
                            attrs {
                                onClickFunction = { signOut(it) }
                            }
                            span(classes = "mdc-button__label") { +"Sign out" }
                        }
                    } else {
                        button(classes = "mdc-button  mdc-button--raised") {
                            attrs {
                                onClickFunction = { setState { isAuthUiVisible = true } }
                            }
                            span(classes = "mdc-button__label") { +"Sign In" }
                        }
                    }
                    div(classes = if (state.isAuthUiVisible) "modal" else "invisible") {
                        div(classes = "modal-content") {
                            attrs {
                                id = "firebaseui-auth-container"
                            }
                            firebaseAuthComponent(authUiConfig, firebaseAuth = firebaseAuth)
                        }
                    }
                }
            }
        }
    }

    private fun signOut(event: Event) {
        logger.info { "Signing out" }
        firebaseAuth.signOut()
    }

    private val authUiConfig: dynamic by lazy {
        val config: dynamic = object {}
        config["signInOptions"] = arrayOf(
            firebase.auth.GoogleAuthProvider.PROVIDER_ID,
            firebase.auth.PhoneAuthProvider.PROVIDER_ID
        )
        config["callbacks"] = object : Callbacks {
            override fun signInSuccessWithAuthResult(
                authResult: dynamic,
                redirectUrl: String?
            ): Boolean {
                logger.info { "signInSuccessWithAuthResult()" }
                return false
            }

            override fun signInFailure(error: AuthUIError): Promise<Unit> {
                logger.info { "signInFailure: ${error.toJSON()}" }
                return Promise.resolve(1).then { }
            }

            override fun uiShown() {
                logger.info { "uiShown()" }
            }
        }
        config
    }
}

interface HomeState : react.RState {
    var isSignedIn: Boolean
    var isAuthUiVisible: Boolean
}

fun RBuilder.home(
    firebaseAuth: FirebaseAuth
) = child(HomeComponent::class) {
    attrs.firebaseAuth = firebaseAuth
}
