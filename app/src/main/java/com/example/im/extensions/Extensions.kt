package com.example.im.extensions

fun String.isValidAccount() = this.matches(Regex("^[a-zA-Z]\\w{2,19}$"))
fun String.isValidPassword() = this.matches(Regex("^\\d{3,20}$"))