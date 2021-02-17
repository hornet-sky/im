package com.example.im.contract

import android.os.Handler
import android.os.Looper

interface BasePresenter<T: BaseView> {
    companion object {
        val handler by lazy { Handler(Looper.getMainLooper()) }
    }
    var view: T?
    // private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    fun uiThread(c: () -> Unit) {
        handler.post(c)
    }

    fun destroy() {
        println("BasePresenter.destroy [ view = $view ]")
        view = null // 相互引用 要防止内存泄漏
    }
}