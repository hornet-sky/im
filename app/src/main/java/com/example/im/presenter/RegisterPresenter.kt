package com.example.im.presenter

import com.example.im.contract.RegisterContract
import com.example.im.extensions.isValidAccount
import com.example.im.extensions.isValidPassword

class RegisterPresenter(override var view: RegisterContract.View?) : RegisterContract.Presenter {
    override fun register(account: String, password: String, passwordConfirm: String) {
        view!!.let {
            if(!account.isValidAccount()) {
                it.onAccountError()
                return
            }
            if(!password.isValidPassword()) {
                it.onPasswordError()
                return
            }
            if(password != passwordConfirm) {
                it.onPasswordConfirmError()
                return
            }
            it.onStartRegister()

        }
    }


}