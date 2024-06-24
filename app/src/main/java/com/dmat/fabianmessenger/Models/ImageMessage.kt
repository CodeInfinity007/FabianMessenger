package com.dmat.fabianmessenger

import java.util.*

class ImageMessage (
    val id: String,
    val img_url: String,
    val toUid: String,
    val fromUid: String,
    val timeStamp: Date
) {
    constructor() : this("", "", "", "", Date(0))
}