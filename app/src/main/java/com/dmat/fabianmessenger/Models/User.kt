package com.dmat.fabianmessenger

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class User(val uid: String, var name: String, val username: String, var dp_url: String, var phone_num: String, var compressImg: Boolean):
    Parcelable {
    constructor() : this("", "", "", "", "", false)
}