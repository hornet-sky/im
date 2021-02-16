package com.example.im.contract

interface SplashContract {
    interface Presenter : BasePresenter {
        fun checkLoginStatus()
    }
    interface View {
        fun onNotLoggedIn()
        fun onLoggedIn()
    }
}