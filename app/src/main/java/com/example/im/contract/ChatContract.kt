package com.example.im.contract

interface ChatContract {
    interface Presenter : BasePresenter<View> {
        fun sendText(message: String, targetAccount: String)
        fun loadMessages(msgId: String?)
    }
    interface View : BaseView {
        fun onStartSendMessage()
        fun onSendMessageSuccess()
        fun onSendMessageFailed(code: Int, message: String?)
    }
}