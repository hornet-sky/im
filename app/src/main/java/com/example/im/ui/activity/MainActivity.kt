package com.example.im.ui.activity

import com.example.im.R
import com.example.im.contract.MainContract
import com.example.im.presenter.MainPresenter
import com.example.im.utils.FragmentUtils
import com.example.im.utils.LogUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), MainContract.View {
    override val presenter by lazy { MainPresenter(this) }
    private var prevTabId: Int = -1

    override fun getLayoutResID() = R.layout.activity_main

    override fun init() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            val tabId = it.itemId
            LogUtils.d("setOnNavigationItemSelected [ tabId = $tabId ]")
            supportFragmentManager.beginTransaction().apply {
                val targetFragment = FragmentUtils.instance.getFragment(tabId)!!
                // replace(R.id.container, targetFragment, tabId.toString())
                if(prevTabId != -1) hide(FragmentUtils.instance.getFragment(prevTabId)!!)
                if(targetFragment.isAdded) show(targetFragment)
                else add(R.id.container, targetFragment)
                commit()
            }
            prevTabId = tabId
            return@setOnNavigationItemSelectedListener true
        }
        bottomNavigationView.selectedItemId = R.id.bottom_navi_bar_item_conversations
    }

    override fun onDestroy() {
        super.onDestroy()
        FragmentUtils.instance.clearAll()
    }
}