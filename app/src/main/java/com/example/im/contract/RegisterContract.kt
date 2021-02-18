package com.example.im.contract

interface RegisterContract {
    companion object {
        val SERVER_TYPE_BMOB = 1
        val SERVER_TYPE_EM = 2
    }
    interface Presenter : BasePresenter<View> {
        fun register(account: String, password: String, passwordConfirm: String)
    }
    interface View : BaseView {
        fun onAccountError()
        fun onPasswordError()
        fun onPasswordConfirmError()
        fun onStartRegister()
        fun onRegisterSuccess(account: String)
        fun onRegisterFailed(serverType: Int, code: Int, message: String?)
    }
}