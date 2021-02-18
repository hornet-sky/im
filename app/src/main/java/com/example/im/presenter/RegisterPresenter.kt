package com.example.im.presenter

import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.example.im.contract.RegisterContract
import com.example.im.extensions.isValidAccount
import com.example.im.extensions.isValidPassword
import com.hyphenate.chat.EMClient
import com.hyphenate.exceptions.HyphenateException

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
            registerBmobUser(account, password)
        }
    }

    private fun registerBmobUser(account: String, password: String) {
        val bmobUser = BmobUser()
        bmobUser.username = account
        bmobUser.setPassword(password)
        bmobUser.signUp(object: SaveListener<BmobUser>() {
            override fun done(bu: BmobUser?, be: BmobException?) {
                be?.let {
                    view!!.onRegisterFailed(RegisterContract.SERVER_TYPE_BMOB, be.errorCode, be.localizedMessage)
                    return
                }
                // 注册环信用户
                registerEMUser(account, password)
            }
        })
    }

    private fun registerEMUser(account: String, password: String) {
        doAsync {
            try {
                // 注册失败会抛出HyphenateException
                EMClient.getInstance().createAccount(account, password)// 同步方法
                uiThread { view!!.onRegisterSuccess(account) }
            } catch (e: HyphenateException) {
                uiThread { view!!.onRegisterFailed(RegisterContract.SERVER_TYPE_EM, e.errorCode, e.localizedMessage) }
            }
        }
    }
}