package com.example.im.ui.activity

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.im.R
import com.example.im.adapter.ChatListAdapter
import com.example.im.adapter.EMMessageListenerAdapter
import com.example.im.contract.ChatContract
import com.example.im.presenter.ChatPresenter
import com.example.im.utils.LogUtils
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.view.*

class ChatActivity : BaseActivity(), ChatContract.View {
    override val presenter = ChatPresenter(this)
    private lateinit var adapter: ChatListAdapter
    private lateinit var messageListener: EMMessageListener
    override fun getLayoutResID() = R.layout.activity_chat
    override fun getTitleText() = getString(R.string.chat_title, presenter.getTargetAccount())
    override fun init() {
        presenter.setTargetAccount(intent.getStringExtra("targetAccount")!!)
        initMessageEditText()
        initSendButton()
        initRecyclerView()
        loadMessages()
        initMessageListener()
    }

    private fun initMessageListener() {
        messageListener = object: EMMessageListenerAdapter() {
            override fun onMessageReceived(msgs: MutableList<EMMessage>?) {
                msgs?.let {
                    presenter.addReceivedEMMessages(msgs)
                }
            }
        }
        EMClient.getInstance().chatManager().addMessageListener(messageListener)
    }

    private fun loadMessages() {
        presenter.loadEMMessages()
    }

    private fun initRecyclerView() {
        with(recyclerView) {
            this@ChatActivity.adapter = ChatListAdapter().apply { submitList(presenter.getEMMessages()) }
            adapter = this@ChatActivity.adapter
            layoutManager = LinearLayoutManager(this@ChatActivity)
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    LogUtils.d("recyclerView.onScrollStateChanged [ newState = $newState ]")
                    if(RecyclerView.SCROLL_STATE_IDLE == newState
                            && (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() == 0) {
                        LogUtils.d("recyclerView.onScrollStateChanged [ loading ]")
                        presenter.loadEMMessages()
                    }
                }
            })
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
            presenter.sendText(message)
        }
    }

    override fun onStartSendMessage() {
        adapter.notifyDataSetChanged()
    }

    override fun onSendMessageSuccess() {
        messageEditText.text.clear()
        adapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(presenter.emMessagesSize() - 1)
    }

    override fun onSendMessageFailed(code: Int, message: String?) {
        LogUtils.d("onLoggedInFailed [ code = $code, message = $message ]")
        adapter.notifyDataSetChanged()
    }

    override fun onReceivedMessagesSuccess() {
        adapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(presenter.emMessagesSize() - 1)
    }

    override fun onStartLoadMessages() {
        adapter.setLoading(true)
        adapter.notifyDataSetChanged()
    }

    override fun onLoadMessagesSuccess(initial: Boolean, loadMsgSize: Int) {
        adapter.setLoading(false)
        adapter.notifyDataSetChanged()
        if (initial) { // 初始化加载
            recyclerView.scrollToPosition(presenter.emMessagesSize() - 1)
        } else if(loadMsgSize > 0) { // 下拉加载更多
            recyclerView.scrollToPosition(loadMsgSize) // 让新消息可见
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EMClient.getInstance().chatManager().removeMessageListener(messageListener)
    }
}