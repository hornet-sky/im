package com.example.im.extensions

import android.content.Context
import com.example.im.model.db.MyDatabaseOpenHelper

fun String.isValidAccount() = this.matches(Regex("^[a-zA-Z]\\w{2,19}$"))
fun String.isValidPassword() = this.matches(Regex("^\\d{3,20}$"))

val Context.myDB: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance()

fun <K, V> MutableMap<K, V>.toPairArray(): Array<Pair<K, V>> {
    return map {
        Pair<K, V>(it.key, it.value)
    }.toTypedArray()
}