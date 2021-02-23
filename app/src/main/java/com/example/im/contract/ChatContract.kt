package com.example.im.contract

import com.hyphenate.chat.EMMessage

interface ChatContract {
    interface Presenter : BasePresenter<View> {
        fun sendText(message: String)
        fun loadEMMessages()
        fun getEMMessages(): MutableList<EMMessage>
        fun emMessagesSize(): Int
        fun addReceivedEMMessages(msgs: MutableList<EMMessage>)
        fun setTargetAccount(targetAccount: String)
        fun getTargetAccount(): String
    }
    interface View : BaseView {
        fun onStartSendMessage()
        fun onSendMessageSuccess()
        fun onSendMessageFailed(code: Int, message: String?)
        fun onReceivedMessagesSuccess()
        fun onLoadMessagesSuccess(initial: Boolean)
    }
}