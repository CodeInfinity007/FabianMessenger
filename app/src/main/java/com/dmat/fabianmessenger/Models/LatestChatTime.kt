package com.dmat.fabianmessenger.Models

import java.util.*

class LatestChatTime(
    val id: String,
    val timeStampdivider: Date,
    val flag: Boolean,
    val toid: String,
    val fromid: String
) {
    constructor() : this("", Date(0), false, "", "")
}