package com.example.im.presenter

import com.example.im.contract.ConversationsContract
import com.example.im.utils.LogUtils
import com.hyphenate.chat.EMClient

class ConversationsPresenter(override var view: ConversationsContract.View?) : ConversationsContract.Presenter {
    override fun loadConversations() { // 当前方法有可能是在主线程中执行，也有可能在子线程中执行
        uiThread { view!!.onStartLoadConversations() }
        doAsync {
            val allConversations = EMClient.getInstance().chatManager().allConversations
            uiThread {
                view!!.onLoadConversationsSuccess(allConversations)
            }
        }
    }

}