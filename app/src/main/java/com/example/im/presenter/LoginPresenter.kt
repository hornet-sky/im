package com.example.im.presenter

import android.provider.SyncStateContract
import android.widget.Toast
import com.example.im.adapter.EMCallBackAdapter
import com.example.im.contract.LoginContract
import com.example.im.extensions.isValidAccount
import com.example.im.extensions.isValidPassword
import com.hyphenate.EMError
import com.hyphenate.chat.EMClient

class LoginPresenter(override var view: LoginContract.View?) : LoginContract.Presenter {
    override fun login(account: String, password: String) {
        view!!.let {
            if(!account.isValidAccount()) {
                it.onAccountError()
                return
            }
            if(!password.isValidPassword()) {
                it.onPasswordError()
                return
            }
            it.onStartLogin()
            loginEM(account, password)
        }

    }

    private fun loginEM(account: String, password: String) {
        EMClient.getInstance().login(account, password, object : EMCallBackAdapter() {
            override fun onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups()
                EMClient.getInstance().chatManager().loadAllConversations()
                uiThread { view!!.onLoggedInSuccess() }
            }

            override fun onError(code: Int, message: String?) {
                uiThread { view!!.onLoggedInFailed(code, message) }
            }
        })
    }
}