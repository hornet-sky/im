package com.example.im.presenter

import com.example.im.contract.SplashContract
import com.hyphenate.chat.EMClient

class SplashPresenter(override var view: SplashContract.View?) : SplashContract.Presenter {
    override fun checkLoginStatus() {
        view?.let {
            if(getLoginStatus()) it.onLoggedIn() else it.onNotLoggedIn()
        }
    }
    private fun getLoginStatus() = EMClient.getInstance().isConnected && EMClient.getInstance().isLoggedInBefore
}