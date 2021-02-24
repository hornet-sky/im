package com.example.im.presenter

import com.example.im.adapter.EMCallBackAdapter
import com.example.im.contract.ChatContract
import com.example.im.utils.LogUtils
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage

class ChatPresenter(override var view: ChatContract.View?) : ChatContract.Presenter {
    companion object {
        const val PAGE_SIZE = 10
    }
    private lateinit var targetAccount: String
    private val emMessages: MutableList<EMMessage> = mutableListOf()
    override fun sendText(message: String) {
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

    override fun loadEMMessages() {
        LogUtils.d("ChatPresenter.loadEMMessages [ targetAccount = $targetAccount ]")
        doAsync {
            EMClient.getInstance().chatManager().getConversation(targetAccount)?.apply {
                markAllMessagesAsRead()
                if(emMessages.isEmpty()) {
                    emMessages.addAll(allMessages)
                    uiThread { view!!.onLoadMessagesSuccess(true, allMessages.size) }
                } else {
                    uiThread { view!!.onStartLoadMessages() }
                    val msgsFromDB = loadMoreMsgFromDB(emMessages.first().msgId, PAGE_SIZE)
                    emMessages.addAll(0, msgsFromDB)
                    uiThread { view!!.onLoadMessagesSuccess(false, msgsFromDB.size) }
                }
            }
        }
    }

    override fun getEMMessages(): MutableList<EMMessage> {
        return emMessages
    }

    override fun emMessagesSize(): Int {
        return emMessages.size
    }

    override fun addReceivedEMMessages(msgs: MutableList<EMMessage>) {
        emMessages.addAll(msgs)
        EMClient.getInstance().chatManager().getConversation(targetAccount)?.markAllMessagesAsRead()
        uiThread { view!!.onReceivedMessagesSuccess() }
    }

    override fun setTargetAccount(targetAccount: String) {
        this.targetAccount = targetAccount
    }

    override fun getTargetAccount(): String {
        return this.targetAccount
    }
}