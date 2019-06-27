package views

import react.RBuilder
import react.ReactElement
import react.dom.*

fun RBuilder.topBar(): ReactElement? = header(classes = "mdc-top-app-bar") {
    div(classes = "mdc-top-app-bar__row") {
        section(classes = "mdc-top-app-bar__section mdc-top-app-bar__section--align-start") {
            button(classes = "material-icons mdc-top-app-bar__navigation-icon mdc-icon-button") { +"menu" }
            span(classes = "mdc-top-app-bar__title") { +"Title" }
        }
        section(classes = "mdc-top-app-bar__section mdc-top-app-bar__section--align-end") {
            button(classes = "material-icons mdc-top-app-bar__action-item mdc-icon-button") {
                attrs["aria-label"] = "Download"
                +"file_download"
            }
        }
    }
}
