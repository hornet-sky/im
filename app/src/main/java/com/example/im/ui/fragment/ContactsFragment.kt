package com.example.im.ui.fragment

import com.example.im.contract.BasePresenter
import com.example.im.contract.ContactsContract
import com.example.im.presenter.ContactsPresenter

class ContactsFragment : BaseFragment(), ContactsContract.View {
    override val presenter = ContactsPresenter(this)

}