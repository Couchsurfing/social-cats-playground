package views

import kotlinx.html.MAIN
import kotlinx.html.attributesMapOf
import react.RBuilder
import react.ReactElement
import react.dom.RDOMBuilder
import react.dom.tag

inline fun RBuilder.main(
    classes: String? = null,
    block: RDOMBuilder<MAIN>.() -> Unit
): ReactElement = tag(block) {
    MAIN(
        attributesMapOf("class", classes), it
    )
}
