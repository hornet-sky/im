package com.example.im.contract

interface RegisterContract {
    interface Presenter : BasePresenter<View> {
        fun register(account: String, password: String, passwordConfirm: String)
    }
    interface View : BaseView {
        fun onAccountError()
        fun onPasswordError()
        fun onPasswordConfirmError()
        fun onStartRegister()
        fun onRegisterSuccess()
        fun onRegisterFailed(code: Int, message: String?)
    }
}