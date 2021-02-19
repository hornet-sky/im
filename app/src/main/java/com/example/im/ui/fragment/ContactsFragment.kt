package com.example.im.ui.fragment

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.im.R
import com.example.im.adapter.ContactsListAdapter
import com.example.im.contract.ContactsContract
import com.example.im.model.ContactsItem
import com.example.im.presenter.ContactsPresenter
import com.example.im.utils.LogUtils
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : BaseFragment(), ContactsContract.View {
    override val presenter = ContactsPresenter(this)
    private lateinit var adapter: ContactsListAdapter

    override fun getResId() = R.layout.fragment_contacts
    override fun getTitle() = getString(R.string.contacts_title)

    override fun initListener() {
        adapter = ContactsListAdapter().apply {
            setOnItemViewClickListener { position, contactsItem ->
                LogUtils.d("onContactsItemClick [ position = $position, contactsItem = ${contactsItem.account} ]")
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
}