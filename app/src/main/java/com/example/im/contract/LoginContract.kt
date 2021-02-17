package com.example.im.contract

interface LoginContract {
    interface Presenter : BasePresenter<View> {
        fun login(account: String, password: String)
    }
    interface View : BaseView {
        fun onAccountError()
        fun onPasswordError()
        fun onStartLogin()
        fun onLoggedInSuccess()
        fun onLoggedInFailed(code: Int, message: String?)
    }
}