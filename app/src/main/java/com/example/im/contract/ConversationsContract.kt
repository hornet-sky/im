package com.example.im.contract

import com.hyphenate.chat.EMConversation

interface ConversationsContract {
    interface Presenter : BasePresenter<View> {
        fun loadConversations()
    }
    interface View : BaseView {
        fun onStartLoadConversations()
        fun onLoadConversationsSuccess(map: Map<String, EMConversation>)
        fun onLoadConversationsFailed(code: Int, message: String?)
    }
}