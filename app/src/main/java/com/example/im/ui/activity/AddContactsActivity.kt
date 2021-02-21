package com.example.im.ui.activity

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.im.R
import com.example.im.adapter.AddContactsListAdapter
import com.example.im.contract.AddContactsContract
import com.example.im.model.AddContactsItem
import com.example.im.presenter.AddContactsPresenter
import com.example.im.utils.LogUtils
import kotlinx.android.synthetic.main.activity_add_contacts.*

class AddContactsActivity : BaseActivity(), AddContactsContract.View {
    override val presenter by lazy { AddContactsPresenter(this) }
    private lateinit var adapter: AddContactsListAdapter
    private lateinit var contacts: MutableList<AddContactsItem>
    override fun init() {
        searchableEditText.apply {
            setOnSearchListener { account ->
                search(account)
            }
            setOnEditorActionListener { _, _, _ ->
                search(text.toString())
                return@setOnEditorActionListener true
            }
        }
        adapter = AddContactsListAdapter().apply {
            setOnAddBtnClickListener { account, position ->
                presenter.sendAddContactRequest(account, position)
            }
        }
        with(recyclerView) {
            adapter = this@AddContactsActivity.adapter
            layoutManager = LinearLayoutManager(this@AddContactsActivity)
            addItemDecoration(DividerItemDecoration(this@AddContactsActivity, RecyclerView.VERTICAL))
        }
    }

    private fun search(account: String) {
        LogUtils.d("searchableEditText [ account = $account ]")
        hideSoftKeyboard()
        presenter.search(account)
    }

    override fun getLayoutResID() = R.layout.activity_add_contacts
    override fun getTitleText() = getString(R.string.contacts_add_title)

    override fun onStartSearch() {
        showProgress(getString(R.string.contacts_searching))
    }

    override fun onSearchSuccess(contacts: MutableList<AddContactsItem>) {
        dismissProgress()
        this.contacts = contacts
        this.adapter.submitList(contacts)
    }

    override fun onSearchFailed(code: Int, message: String?) {
        LogUtils.d("onSearchFailed [ code = $code, message = $message ]")
        dismissProgress()
        toast(getString(R.string.contacts_search_failed))
    }

    override fun onStartSendAddContactRequest() {
        showProgress(getString(R.string.contacts_add_req_sending))
    }

    override fun onSendAddContactRequestSuccess(account: String, position: Int) {
        dismissProgress()
        toast(getString(R.string.contacts_add_req_send_success))
    }

    override fun onSendAddContactRequestFailed(code: Int, message: String?) {
        LogUtils.d("onAddFailed [ code = $code, message = $message ]")
        dismissProgress()
        toast(getString(R.string.contacts_add_req_send_failed))
    }
}