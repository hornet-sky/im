package com.example.im.presenter

import com.example.im.contract.ContactsContract
import com.example.im.model.ContactsItem
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient

class ContactsPresenter(override var view: ContactsContract.View?) : ContactsContract.Presenter {
    override fun loadContacts() {
        EMClient.getInstance().contactManager().aysncGetAllContactsFromServer(object: EMValueCallBack<List<String>> {
            override fun onSuccess(accounts: List<String>?) {
                val contacts = mutableListOf<ContactsItem>()
                // accounts!!
                mutableListOf("tom", "alice", "frank", "jack", "king", "820", "Li", "alan", "Lucy").forEach { account ->
                    contacts.add(ContactsItem(account.substring(0, 1), account))
                }
                contacts.sortBy { it.account.toUpperCase() }
                uiThread { view!!.onLoadContactsSuccess(contacts) }
            }
            override fun onError(code: Int, message: String?) {
                uiThread { view!!.onLoadContactsFailed(code, message) }
            }
        })

    }

}