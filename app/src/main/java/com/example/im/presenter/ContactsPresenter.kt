package com.example.im.presenter

import com.example.im.adapter.EMCallBackAdapter
import com.example.im.contract.ContactsContract
import com.example.im.model.ContactsItem
import com.example.im.utils.LogUtils
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient
import java.util.*

class ContactsPresenter(override var view: ContactsContract.View?) : ContactsContract.Presenter {
    override fun loadContacts() {
        view!!.onStartLoadContacts()
        EMClient.getInstance().contactManager().aysncGetAllContactsFromServer(object: EMValueCallBack<List<String>> {
            override fun onSuccess(accounts: List<String>?) {
                val contacts = mutableListOf<ContactsItem>()
                // accounts!!
                mutableListOf("tom", "alice", "frank", "jack", "king", "820", "Li", "alan", "Lucy", "albert", "einstein", "maxwell", "black", "white", "wong", "smith", "bob", "jack", "jerry", "john").forEach { account ->
                    var firstChar = account.first()
                    if(!(firstChar in 'A'..'Z' || firstChar in 'a'..'z')) {
                        firstChar = '#'
                    }
                    contacts.add(ContactsItem(firstChar.toString(), account))
                }
                contacts.sortBy { it.account.toUpperCase(Locale.ROOT) }
                uiThread { view!!.onLoadContactsSuccess(contacts) }
            }
            override fun onError(code: Int, message: String?) {
                uiThread { view!!.onLoadContactsFailed(code, message) }
            }
        })

    }

    override fun deleteContact(contact: ContactsItem) {
        view!!.onStartDeleteContact()
        EMClient.getInstance().contactManager().aysncDeleteContact(contact.account, object: EMCallBackAdapter() {
            override fun onSuccess() {
                uiThread {
                    loadContacts()
                    view!!.onDeleteContactSuccess(contact)
                }
            }

            override fun onError(code: Int, message: String?) {
                uiThread { view!!.onDeleteContactFailed(code, message) }
            }
        })
    }

}