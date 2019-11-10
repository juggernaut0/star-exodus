package ui

import kui.MarkupBuilder
import kui.classes

inline fun MarkupBuilder.row(block: MarkupBuilder.() -> Unit) = div(classes("row"), block)
inline fun MarkupBuilder.col3(block: MarkupBuilder.() -> Unit) = div(classes("col-3"), block)
inline fun MarkupBuilder.col4(block: MarkupBuilder.() -> Unit) = div(classes("col-4"), block)
inline fun MarkupBuilder.col6(block: MarkupBuilder.() -> Unit) = div(classes("col-6"), block)
inline fun MarkupBuilder.col9(block: MarkupBuilder.() -> Unit) = div(classes("col-9"), block)
inline fun MarkupBuilder.col12(block: MarkupBuilder.() -> Unit) = div(classes("col-12"), block)
inline fun MarkupBuilder.colMd3(block: MarkupBuilder.() -> Unit) = div(classes("col-md-3"), block)
inline fun MarkupBuilder.colMd4(block: MarkupBuilder.() -> Unit) = div(classes("col-md-4"), block)
inline fun MarkupBuilder.colMd6(block: MarkupBuilder.() -> Unit) = div(classes("col-md-6"), block)
inline fun MarkupBuilder.colMd9(block: MarkupBuilder.() -> Unit) = div(classes("col-md-9"), block)
