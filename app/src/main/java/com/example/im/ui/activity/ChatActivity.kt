package com.example.im.ui.activity

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.im.R
import com.example.im.adapter.ChatListAdapter
import com.example.im.contract.ChatContract
import com.example.im.model.ContactsItem
import com.example.im.presenter.ChatPresenter
import com.example.im.utils.LogUtils
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.view.*

class ChatActivity : BaseActivity(), ChatContract.View {
    override val presenter = ChatPresenter(this)
    private lateinit var targetAccount: String
    private lateinit var adapter: ChatListAdapter
    override fun getLayoutResID() = R.layout.activity_chat
    override fun getTitleText() = getString(R.string.chat_title, targetAccount)
    override fun init() {
        targetAccount = intent.getParcelableExtra<ContactsItem>("target")!!.account
        initMessageEditText()
        initSendButton()
        initRecyclerView()
        loadMessages()
    }

    private fun loadMessages() {
        presenter.loadMessages(null)
    }

    private fun initRecyclerView() {
        with(recyclerView) {
            this@ChatActivity.adapter = ChatListAdapter().apply { submitList(presenter.getEMMessages()) }
            adapter = this@ChatActivity.adapter
            layoutManager = LinearLayoutManager(this@ChatActivity)
        }
    }

    private fun initSendButton() {
        sendButton.setOnClickListener { sendMessage() }
    }

    private fun initMessageEditText() {
        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                editable?.let {
                    sendButton.isEnabled = !TextUtils.isEmpty(it.toString())
                }
            }
        })
        messageEditText.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            return@setOnEditorActionListener true
        }
    }

    private fun sendMessage() {
        val message = messageEditText.text.toString()
        if(!TextUtils.isEmpty(message)) {
            presenter.sendText(message, targetAccount)
        }
    }

    override fun onStartSendMessage() {
        adapter.notifyDataSetChanged()
    }

    override fun onSendMessageSuccess() {
        messageEditText.text.clear()
        adapter.notifyDataSetChanged()
    }

    override fun onSendMessageFailed(code: Int, message: String?) {
        LogUtils.d("onLoggedInFailed [ code = $code, message = $message ]")
        adapter.notifyDataSetChanged()
    }
}