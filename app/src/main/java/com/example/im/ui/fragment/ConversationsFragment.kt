package com.example.im.ui.fragment

import com.example.im.contract.ConversationsContract
import com.example.im.presenter.ConversationsPresenter

class ConversationsFragment : BaseFragment(), ConversationsContract.View {
    override val presenter = ConversationsPresenter(this)

}