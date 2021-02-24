package com.example.im.ui.fragment

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.im.R
import com.example.im.adapter.ConversationsListAdapter
import com.example.im.adapter.EMMessageListenerAdapter
import com.example.im.contract.ConversationsContract
import com.example.im.model.ConversationItem
import com.example.im.presenter.ConversationsPresenter
import com.example.im.ui.activity.ChatActivity
import com.example.im.utils.LogUtils
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import kotlinx.android.synthetic.main.fragment_contacts.*

class ConversationsFragment : BaseFragment(), ConversationsContract.View {
    override val presenter by lazy { ConversationsPresenter(this) }
    private lateinit var adapter: ConversationsListAdapter
    private lateinit var emMessageListener: EMMessageListenerAdapter

    override fun getTitle() = getString(R.string.conversations_title)
    override fun getResId() = R.layout.fragment_conversations

    override fun initListener() {
        initRecyclerViewListener()
        initEMMessageListener()
    }

    private fun initEMMessageListener() {
        emMessageListener = object: EMMessageListenerAdapter() {
            override fun onMessageReceived(msgs: MutableList<EMMessage>?) {
                loadConversations()
            }
        }
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener)
    }

    override fun initData() {
        loadConversations()
    }

    private fun loadConversations() {
        LogUtils.d("ConversationsFragment.loadConversations")
        presenter.loadConversations()
    }

    private fun initRecyclerViewListener() {
        adapter = ConversationsListAdapter().apply {
            setOnItemViewClickListener { _, conversationItem ->
                startActivity<ChatActivity>("targetAccount" to conversationItem.targetAccount)
            }
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                addItemDecoration(DividerItemDecoration(activity, RecyclerView.VERTICAL))
            }.adapter = this
        }
    }

    override fun onStartLoadConversations() {
        showProgress(getString(R.string.conversations_loading))
    }

    override fun onLoadConversationsSuccess(map: Map<String, EMConversation>) {
        LogUtils.d("ConversationsFragment.onLoadConversationsSuccess $map")
        dismissProgress()
        val conversations = mutableListOf<ConversationItem>()
        for ((k, v) in map) {
            conversations.add(ConversationItem(v, k))
        }
        conversations.sortByDescending { it.conversation.lastMessage.msgTime }
        adapter.submitList(conversations)
    }

    override fun onLoadConversationsFailed(code: Int, message: String?) {
        LogUtils.d("onLoadConversationsFailed [ code = $code, message = $message ]")
        dismissProgress()
        toast(getString(R.string.loading_failed, getString(R.string.conversations_title)))
    }

    override fun onResume() {
        super.onResume()
        loadConversations()
    }

    override fun onDestroy() {
        super.onDestroy()
        EMClient.getInstance().chatManager().removeMessageListener(emMessageListener)
    }
}