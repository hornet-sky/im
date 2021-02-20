package com.example.im.contract

import com.example.im.model.ContactsItem

interface ContactsContract {
    interface Presenter : BasePresenter<View> {
        fun loadContacts()
        fun deleteContact(contact: ContactsItem)
    }
    interface View : BaseView {
        fun onStartLoadContacts()
        fun onLoadContactsSuccess(contacts: MutableList<ContactsItem>)
        fun onLoadContactsFailed(code: Int, message: String?)
        fun onStartDeleteContact()
        fun onDeleteContactSuccess(contact: ContactsItem)
        fun onDeleteContactFailed(code: Int, message: String?)
    }
}