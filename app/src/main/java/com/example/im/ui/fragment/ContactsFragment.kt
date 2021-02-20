package com.example.im.ui.fragment

import android.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.im.R
import com.example.im.adapter.ContactsListAdapter
import com.example.im.contract.ContactsContract
import com.example.im.model.ContactsItem
import com.example.im.presenter.ContactsPresenter
import com.example.im.ui.activity.ChatActivity
import com.example.im.utils.LogUtils
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : BaseFragment(), ContactsContract.View {
    override val presenter by lazy { ContactsPresenter(this) }
    private lateinit var adapter: ContactsListAdapter

    override fun getResId() = R.layout.fragment_contacts
    override fun getTitle() = getString(R.string.contacts_title)

    override fun initListener() {
        adapter = ContactsListAdapter().apply {
            setOnItemViewClickListener { position, contactsItem ->
                LogUtils.d("onContactsItemClick [ position = $position, contactsItem = ${contactsItem.account} ]")
                startActivity<ChatActivity>("target" to contactsItem)
            }
            setOnItemViewLongClickListener { position, contactsItem ->
                LogUtils.d("onContactsItemLongClick [ position = $position, contactsItem = ${contactsItem.account} ]")
                AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.contacts_delete_title))
                    .setMessage(getString(R.string.contacts_delete_message, contactsItem.account))
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定") { _, _ ->
                        presenter.deleteContact(contactsItem)
                    }
                    .show()
            }
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                addItemDecoration(DividerItemDecoration(activity, VERTICAL))
            }.adapter = this
        }
        swipeRefreshLayout.apply {
            setColorSchemeColors(resources.getColor(R.color.qq_blue, null))
            setOnRefreshListener { presenter.loadContacts() }
        }
    }

    override fun initData() {
        presenter.loadContacts()
    }

    override fun onStartLoadContacts() {
        if(!swipeRefreshLayout.isRefreshing)
            swipeRefreshLayout.isRefreshing = true
    }

    override fun onLoadContactsSuccess(contacts: MutableList<ContactsItem>) {
        swipeRefreshLayout.isRefreshing = false
        adapter.submitList(contacts)
    }

    override fun onLoadContactsFailed(code: Int, message: String?) {
        swipeRefreshLayout.isRefreshing = false
        LogUtils.d("onLoadContactsFailed [ code = $code, message = $message ]")
        toast(getString(R.string.loading_failed, getString(R.string.contacts_title)))
    }

    override fun onStartDeleteContact() {
        showProgress(getString(R.string.contacts_deleting))
    }

    override fun onDeleteContactSuccess(contact: ContactsItem) {
        dismissProgress()
        toast(getString(R.string.contacts_delete_success))
    }

    override fun onDeleteContactFailed(code: Int, message: String?) {
        LogUtils.d("onDeleteContactFailed [ code = $code, message = $message ]")
        dismissProgress()
        toast(getString(R.string.contacts_delete_failed))
    }
}