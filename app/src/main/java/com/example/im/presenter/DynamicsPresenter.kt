package com.example.im.presenter

import com.example.im.adapter.EMCallBackAdapter
import com.example.im.contract.DynamicsContract
import com.hyphenate.chat.EMClient

class DynamicsPresenter(override var view: DynamicsContract.View?) : DynamicsContract.Presenter {
    override fun logout() {
        view!!.let {
            it.onStartLogout()
            logoutEM()
        }
    }

    private fun logoutEM() {
        EMClient.getInstance().logout(true, object : EMCallBackAdapter() {
            override fun onSuccess() {
                uiThread { view!!.onLogoutSuccess() }
            }
            override fun onError(code: Int, message: String?) {
                uiThread { view!!.onLogoutFailed(code, message) }
            }
        })
    }
}