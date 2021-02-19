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

    fun doAsync(c: () -> Unit) {
        Thread {
            c.invoke()
        }.start()
    }

    fun destroy() {
        println("BasePresenter.destroy [ view = $view ]")
        view = null // 解除相互引用 防止内存泄漏
    }
}