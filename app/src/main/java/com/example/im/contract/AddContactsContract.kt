package com.example.im.contract

import com.example.im.model.AddContactsItem

interface AddContactsContract {
    interface Presenter : BasePresenter<View> {
        fun search(account: String)
        fun sendAddContactRequest(account: String, position: Int)
    }
    interface View : BaseView {
        fun onStartSearch()
        fun onSearchSuccess(contacts: MutableList<AddContactsItem>)
        fun onSearchFailed(code: Int, message: String?)
        fun onStartSendAddContactRequest()
        fun onSendAddContactRequestSuccess(account: String, position: Int)
        fun onSendAddContactRequestFailed(code: Int, message: String?)
    }
}