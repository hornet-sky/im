package com.example.im.contract

import com.example.im.model.ContactsItem

interface ContactsContract {
    interface Presenter : BasePresenter<View> {
        fun loadContacts()
    }
    interface View : BaseView {
        fun onStartLoadContacts()
        fun onLoadContactsSuccess(contacts: MutableList<ContactsItem>)
        fun onLoadContactsFailed(code: Int, message: String?)
    }
}