package com.example.im.presenter

import com.example.im.contract.SplashContract

class SplashPresenter(private var view: SplashContract.View?) : SplashContract.Presenter {
    override fun checkLoginStatus() {
        view?.let {
            if(getLoginStatus()) it.onLoggedIn() else it.onNotLoggedIn()
        }
    }
    fun getLoginStatus() = false
    fun destroy() {
        view = null // 相互引用 要防止内存泄漏
    }
}