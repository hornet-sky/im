package com.example.im.presenter

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.example.im.adapter.EMCallBackAdapter
import com.example.im.contract.AddContactsContract
import com.example.im.model.AddContactsItem
import com.example.im.model.db.ContactsDBOperator
import com.example.im.utils.LogUtils
import com.hyphenate.chat.EMClient


class AddContactsPresenter(override var view: AddContactsContract.View?) : AddContactsContract.Presenter {
    override fun search(account: String) {
        searchBmobUser(account)
    }
    override fun sendAddContactRequest(account: String, position: Int) {
        addEMContact(account, position)
    }

    private fun addEMContact(account: String, position: Int) {
        LogUtils.d("addEMContact [ account = $account, position = $position ]")
        view!!.onStartSendAddContactRequest()
        EMClient.getInstance().contactManager().aysncAddContact(account, null, object: EMCallBackAdapter() {
            override fun onSuccess() {
                uiThread { view!!.onSendAddContactRequestSuccess(account, position) }
            }
            override fun onError(code: Int, message: String?) {
                uiThread { view!!.onSendAddContactRequestFailed(code, message) }
            }
        })
    }

    private fun searchBmobUser(account: String) {
        view!!.onStartSearch()
        val bmobQuery = BmobQuery<BmobUser>()
                .addWhereContains("username", account)
                .addWhereNotEqualTo("username", EMClient.getInstance().currentUser)
        doAsync {
            bmobQuery.findObjects(object: FindListener<BmobUser>() {
                override fun done(users: MutableList<BmobUser>, e: BmobException?) {
                    if(e != null) {
                        uiThread { view!!.onSearchFailed(e.errorCode, e.localizedMessage) }
                        return
                    }
                    val friends = ContactsDBOperator.listAll()
                    val contacts = mutableListOf<AddContactsItem>()
                    var isFriend: Boolean
                    for (user in users) {
                        isFriend = false
                        for(friend in friends) {
                            if(friend.account == user.username) {
                                isFriend = true
                                break
                            }
                        }
                        contacts.add(AddContactsItem(user.username, user.createdAt, isFriend))
                    }
                    uiThread { view!!.onSearchSuccess(contacts) }
                }
            })
        }
    }
}