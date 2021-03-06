package com.example.im.contract

interface SplashContract {
    interface Presenter : BasePresenter<View> {
        fun checkLoginStatus()
    }
    interface View : BaseView {
        fun onNotLoggedIn()
        fun onLoggedIn()
    }
}