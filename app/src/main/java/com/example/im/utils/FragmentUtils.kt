package com.example.im.utils

import com.example.im.R
import com.example.im.ui.fragment.BaseFragment
import com.example.im.ui.fragment.ContactsFragment
import com.example.im.ui.fragment.ConversationsFragment
import com.example.im.ui.fragment.DynamicsFragment

class FragmentUtils private constructor() {
    private var conversationsFragment: ConversationsFragment? = null
    private var contactsFragment: ContactsFragment? = null
    private var dynamicsFragment: DynamicsFragment? = null
    // 单例模式（懒汉）
    companion object {
        val instance by lazy { FragmentUtils() }
    }

    fun getFragment(resId: Int): BaseFragment? {
        return when(resId) {
            R.id.bottom_navi_bar_item_conversations -> {
                if(this.conversationsFragment == null) {
                    this.conversationsFragment = ConversationsFragment()
                }
                this.conversationsFragment
            }
            R.id.bottom_navi_bar_item_contacts -> {
                if(this.contactsFragment == null) {
                    this.contactsFragment = ContactsFragment()
                }
                this.contactsFragment
            }
            R.id.bottom_navi_bar_item_dynamics -> {
                if(dynamicsFragment == null) {
                    dynamicsFragment = DynamicsFragment()
                }
                dynamicsFragment
            }
            else -> null
        }
    }

    fun clearAll() {
        this.conversationsFragment = null
        this.contactsFragment = null
        this.dynamicsFragment = null
    }
}