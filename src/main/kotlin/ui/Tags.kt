package ui

import kui.AbstractMarkupBuilder
import kui.MarkupBuilder
import kui.classes

inline fun AbstractMarkupBuilder.row(block: MarkupBuilder.() -> Unit) = div(classes("row"), block)
inline fun AbstractMarkupBuilder.col3(block: MarkupBuilder.() -> Unit) = div(classes("col-3"), block)
inline fun AbstractMarkupBuilder.col6(block: MarkupBuilder.() -> Unit) = div(classes("col-6"), block)
inline fun AbstractMarkupBuilder.col9(block: MarkupBuilder.() -> Unit) = div(classes("col-9"), block)
inline fun AbstractMarkupBuilder.colMd3(block: MarkupBuilder.() -> Unit) = div(classes("col-md-3"), block)
inline fun AbstractMarkupBuilder.colMd4(block: MarkupBuilder.() -> Unit) = div(classes("col-md-4"), block)
inline fun AbstractMarkupBuilder.colMd6(block: MarkupBuilder.() -> Unit) = div(classes("col-md-6"), block)
inline fun AbstractMarkupBuilder.colMd9(block: MarkupBuilder.() -> Unit) = div(classes("col-md-9"), block)
