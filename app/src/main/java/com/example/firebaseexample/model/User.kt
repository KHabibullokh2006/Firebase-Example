package com.example.firebaseexample.model

import java.io.Serializable

data class User(val name: String? = null, val uid: String? = null, val photo: String? = null) : Serializable{
    constructor() : this(null, null, null)
}
