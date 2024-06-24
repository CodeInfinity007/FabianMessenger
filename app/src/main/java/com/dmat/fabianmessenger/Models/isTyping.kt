package com.dmat.fabianmessenger


class isTyping(
    val to: Boolean,
    val from: Boolean,
    val fromid: String,
    val toUid: String
) {
    constructor() : this(false, false, "", "")
}