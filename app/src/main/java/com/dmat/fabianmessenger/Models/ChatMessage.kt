package com.dmat.fabianmessenger

import java.util.Date


class ChatMessage(
    val id: String,
    val reaction: Int,
    var text: String,
    val toUid: String,
    val fromUid: String,
    val timeStamp: Date
) {
    constructor() : this("", -1, "", "", "", Date(0))
}