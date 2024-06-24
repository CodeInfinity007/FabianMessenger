package com.dmat.fabianmessenger.Models

import android.net.Uri
import java.util.*

class HelpCentreModel(
    val id: String,
    val text: String,
    val ss: String?,
    val email: String,
    val toUid: String
) {
    constructor() : this("", "", "", "","")
}