package com.example.im.presenter

import com.example.im.adapter.EMCallBackAdapter
import com.example.im.contract.ChatContract
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage

class ChatPresenter(override var view: ChatContract.View?) : ChatContract.Presenter {
    private val emMessages: MutableList<EMMessage> = mutableListOf()
    override fun sendText(message: String, targetAccount: String) {
        with(EMMessage.createTxtSendMessage(message, targetAccount)) {
            setMessageStatusCallback(object: EMCallBackAdapter() {
                override fun onSuccess() {
                    uiThread { view!!.onSendMessageSuccess() }
                }
                override fun onError(code: Int, message: String?) {
                    uiThread { view!!.onSendMessageFailed(code, message) }
                }
            })
            emMessages.add(this)
            view!!.onStartSendMessage()
            EMClient.getInstance().chatManager().sendMessage(this)
        }

    }

    override fun loadMessages(msgId: String?) {

    }

    fun getEMMessages(): MutableList<EMMessage> {
        return emMessages
    }
}