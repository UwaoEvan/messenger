package com.evan.mymessenger.models

class User(val uid: String, val userName: String, val profileImageUrl: String) {
    constructor() : this("", "", "") {
    }
}