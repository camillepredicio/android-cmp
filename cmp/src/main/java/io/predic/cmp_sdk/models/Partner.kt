package io.predic.cmp_sdk.models

data class Partner(
    val key: String,
    val name: String,
    val text: String,
    var state: Boolean = false
)