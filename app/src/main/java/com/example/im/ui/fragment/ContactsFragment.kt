package com.example.im.ui.fragment

import android.app.AlertDialog
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.im.R
import com.example.im.adapter.ContactsListAdapter
import com.example.im.adapter.EMContactListenerAdapter
import com.example.im.contract.ContactsContract
import com.example.im.model.ContactsItem
import com.example.im.presenter.ContactsPresenter
import com.example.im.ui.activity.AddContactsActivity
import com.example.im.ui.activity.ChatActivity
import com.example.im.utils.LogUtils
import com.example.im.widget.ContactsIndexView
import com.hyphenate.EMContactListener
import com.hyphenate.chat.EMClient
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.android.synthetic.main.top_action_bar.*
import java.util.*


class ContactsFragment : BaseFragment(), ContactsContract.View {
    override val presenter by lazy { ContactsPresenter(this) }
    private lateinit var adapter: ContactsListAdapter
    private lateinit var emContactListener: EMContactListener

    override fun getResId() = R.layout.fragment_contacts
    override fun getTitle() = getString(R.string.contacts_title)

    override fun initListener() {
        initRecyclerViewListener()
        initContactsIndexViewListener()
        initAddBtnListener()
        initSwipeRefreshLayoutListener()
        initEMContactListener()
    }

    private fun initEMContactListener() {
        emContactListener = object : EMContactListenerAdapter() {
            override fun onContactDeleted(account: String?) {
                LogUtils.d("emContactListener.onContactDeleted [ account = $account ]")
                presenter.loadContacts()
            }
            override fun onContactAdded(account: String?) {
                LogUtils.d("emContactListener.onContactAdded [ account = $account ]")
                presenter.loadContacts()
            }
        }
        EMClient.getInstance().contactManager().setContactListener(emContactListener)
    }

    private fun initSwipeRefreshLayoutListener() {
        swipeRefreshLayout.apply {
            setColorSchemeColors(resources.getColor(R.color.qq_blue, null))
            setOnRefreshListener { presenter.loadContacts() }
        }
    }

    private fun initAddBtnListener() {
        addBtn.visibility = View.VISIBLE
        addBtn.setOnClickListener {
            startActivity<AddContactsActivity>()
        }
    }

    private fun initContactsIndexViewListener() {
        contactsIndexView.setOnSlidingListener(object : ContactsIndexView.OnSlidingListener {
            override fun onSliding(letter: String) {
                currentLetterTextView.text = letter
                currentLetterTextView.visibility = View.VISIBLE
                /*
                val itemPosition = getItemPosition(letter)
                if (itemPosition != -1) {
                    recyclerView.smoothScrollToPosition(itemPosition)
                }
                 */
                smoothMoveTo(letter)
            }

            override fun onSlidingRelease() {
                currentLetterTextView.visibility = View.GONE
            }
        })
    }

    private fun initRecyclerViewListener() {
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
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                            mShouldScroll = false
                            smoothMoveTo(mToPosition)
                        }
                    }
                })
            }.adapter = this
        }
    }

    //目标项是否在最后一个可见项之后
    private var mShouldScroll = false
    //记录目标项位置
    private var mToPosition = 0
    /**
     * 滑动到指定位置
     */
    private fun smoothMoveTo(letter: String) {
        val position = getItemPosition(letter)
        if (position == -1) {
            return
        }
        smoothMoveTo(position)
    }

    private fun smoothMoveTo(position: Int) {
        // 第一个可见位置
        val firstItem = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(0))
        // 最后一个可见位置
        val lastItem = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(recyclerView.childCount - 1))
        LogUtils.d("firstItem = $firstItem, lastItem = $lastItem, currentPosition = $position")
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前
            recyclerView.smoothScrollToPosition(position)
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后
            val movePosition = position - firstItem
            if (movePosition >= 0 && movePosition < recyclerView.childCount) {
                val top = recyclerView.getChildAt(movePosition).top
                recyclerView.smoothScrollBy(0, top)
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            recyclerView.smoothScrollToPosition(position)
            mToPosition = position
            mShouldScroll = true
        }
    }

    private fun getItemPosition(firstLetter: String): Int {
        for((position, item) in adapter.currentList.withIndex()) {
            if(item.firstLetter.toUpperCase(Locale.ROOT) == firstLetter) {
                return position
            }
        }
        return -1
    }

    override fun initData() {
        presenter.loadContacts()
    }

    override fun onStartLoadContacts() {
        if(!swipeRefreshLayout.isRefreshing)
            swipeRefreshLayout.post {
                swipeRefreshLayout.isRefreshing = true
            }
    }

    override fun onLoadContactsSuccess(contacts: MutableList<ContactsItem>) {
        LogUtils.d("onLoadContactsSuccess [ contacts = $contacts ]")
        swipeRefreshLayout.isRefreshing = false
        adapter.submitList(contacts)
    }

    override fun onLoadContactsFailed(code: Int, message: String?) {
        LogUtils.d("onLoadContactsFailed [ code = $code, message = $message ]")
        swipeRefreshLayout.isRefreshing = false
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

    override fun onDestroy() {
        super.onDestroy()
        EMClient.getInstance().contactManager().removeContactListener(emContactListener)
    }
}