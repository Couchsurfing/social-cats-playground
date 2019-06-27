package components

import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import views.termsView
import views.topBar

class TermsComponent : RComponent<RProps, RState>() {
    override fun RBuilder.render() {
        topBar()
        termsView()
    }
}
