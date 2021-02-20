package com.example.im.ui.activity

import com.example.im.R
import com.example.im.contract.ChatContract
import com.example.im.presenter.ChatPresenter

class ChatActivity : BaseActivity(), ChatContract.View {
    override val presenter = ChatPresenter(this)

    override fun getLayoutResID() = R.layout.activity_chat

}