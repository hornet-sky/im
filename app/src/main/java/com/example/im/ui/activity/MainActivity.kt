package com.example.im.ui.activity

import com.example.im.R
import com.example.im.adapter.EMMessageListenerAdapter
import com.example.im.contract.MainContract
import com.example.im.presenter.MainPresenter
import com.example.im.utils.FragmentUtils
import com.example.im.utils.LogUtils
import com.hyphenate.EMConnectionListener
import com.hyphenate.EMError
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), MainContract.View {
    override val presenter by lazy { MainPresenter(this) }
    private var prevTabId: Int = -1
    private lateinit var emMessageListener: EMMessageListenerAdapter
    private lateinit var emConnListener: EMConnectionListener

    override fun getLayoutResID() = R.layout.activity_main

    override fun init() {
        initBottomNavigationView()
        initEMMessageListener()
        initEMConnectionListener()
    }

    private fun initEMConnectionListener() {
        emConnListener = object: EMConnectionListener {
            override fun onConnected() {
            }
            override fun onDisconnected(code: Int) {
                runOnUiThread {
                    LogUtils.d("onDisconnected [ code = $code ]")
                    if(code == EMError.USER_LOGIN_ANOTHER_DEVICE) { // 好像检查不到多设备登录
                        startActivityThenFinish<LoginActivity>()
                        toast(getString(R.string.user_login_another_device))
                    }
                }
            }
        }
        LogUtils.d("initEMConnectionListener")
        EMClient.getInstance().addConnectionListener(emConnListener)
    }

    private fun initEMMessageListener() {
        emMessageListener = object: EMMessageListenerAdapter() {
            override fun onMessageReceived(msgs: MutableList<EMMessage>?) {
                runOnUiThread { refreshConversationBottomBarBadge() }
            }
        }
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener)
    }

    private fun initBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            val tabId = it.itemId
            LogUtils.d("setOnNavigationItemSelected [ tabId = $tabId ]")
            supportFragmentManager.beginTransaction().apply {
                val targetFragment = FragmentUtils.instance.getFragment(tabId)!!
                // replace(R.id.container, targetFragment, tabId.toString())
                if (prevTabId != -1) hide(FragmentUtils.instance.getFragment(prevTabId)!!)
                if (targetFragment.isAdded) show(targetFragment)
                else add(R.id.container, targetFragment)
                commit()
            }
            prevTabId = tabId
            return@setOnNavigationItemSelectedListener true
        }
        bottomNavigationView.selectedItemId = R.id.bottom_navi_bar_item_conversations
    }

    override fun onResume() {
        super.onResume()
        refreshConversationBottomBarBadge()
    }

    private fun refreshConversationBottomBarBadge() {
        val unreadMessageCount = EMClient.getInstance().chatManager().unreadMessageCount
        if (unreadMessageCount > 0)
            bottomNavigationView.getOrCreateBadge(R.id.bottom_navi_bar_item_conversations)?.let {
                it.number = unreadMessageCount
            }
        else bottomNavigationView.removeBadge(R.id.bottom_navi_bar_item_conversations)
    }

    override fun onDestroy() {
        super.onDestroy()
        FragmentUtils.instance.clearAll()
        EMClient.getInstance().chatManager().removeMessageListener(emMessageListener)
        EMClient.getInstance().removeConnectionListener(emConnListener)
    }
}