package com.example.im.model.db

import com.example.im.extensions.toPairArray

data class Contact(val map: MutableMap<String, Any?>) {
    val _id by map
    val account by map
    fun toPairArray(): Array<Pair<String, Any?>> {
        return map.toPairArray()
    }
}