package com.example.im.contract

interface DynamicsContract {
    interface Presenter : BasePresenter<View> {
        fun logout()
    }
    interface View : BaseView {
        fun onStartLogout()
        fun onLogoutSuccess()
        fun onLogoutFailed(code: Int, message: String?)
    }
}