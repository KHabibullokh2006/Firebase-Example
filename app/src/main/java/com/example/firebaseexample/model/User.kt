package com.example.firebaseexample.model

data class User(val name: String? = null, val uid: String? = null, val photo: String? = null) {
    constructor() : this(null, null, null)
}
