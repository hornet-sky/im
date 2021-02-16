package com.example.im.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResID())
        init()
    }

    open fun init() {}

    abstract fun getLayoutResID(): Int

    inline fun <reified T> startActivity() {
        val intent =  Intent(this, T::class.java)
        startActivity(intent)
    }
}
