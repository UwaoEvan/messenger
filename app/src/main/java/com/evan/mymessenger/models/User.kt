package com.evan.mymessenger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val userName: String, val profileImageUrl: String):Parcelable {
    constructor() : this("", "", "")
}