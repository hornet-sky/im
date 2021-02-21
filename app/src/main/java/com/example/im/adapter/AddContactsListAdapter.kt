package com.example.im.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.im.R
import com.example.im.model.AddContactsItem

class AddContactsListAdapter : ListAdapter<AddContactsItem, AddContactsListAdapter.ViewHolder>(object: DiffUtil.ItemCallback<AddContactsItem>() {
    override fun areItemsTheSame(oldItem: AddContactsItem, newItem: AddContactsItem) = oldItem == newItem
    override fun areContentsTheSame(oldItem: AddContactsItem, newItem: AddContactsItem) = oldItem.account == newItem.account
}) {
    private var onAddBtnClick: ((String, Int) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(parent.context).inflate(R.layout.add_contacts_list_item, parent, false).let {
            ViewHolder(it).apply {
                bindAddButtonClickListener { account -> onAddBtnClick?.invoke(account, adapterPosition) }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatarImageView by lazy { itemView.findViewById<ImageView>(R.id.avatarImageView) }
        private val accountTextView by lazy { itemView.findViewById<TextView>(R.id.accountTextView) }
        private val createTimeTextView by lazy { itemView.findViewById<TextView>(R.id.createTimeTextView) }
        private val addButton by lazy { itemView.findViewById<Button>(R.id.addButton) }
        fun bind(itemData: AddContactsItem) {
            accountTextView.text = itemData.account
            createTimeTextView.text = itemData.createTime
            if(itemData.isAdd) {
                addButton.text = itemView.context.getString(R.string.add_button_reverse_label)
                addButton.isEnabled = false
            } else {
                addButton.text = itemView.context.getString(R.string.add_button_label)
                addButton.isEnabled = true
            }
        }
        fun bindAddButtonClickListener(onAddBtnClick: ((String) -> Unit)?) {
            addButton.setOnClickListener { onAddBtnClick?.invoke(accountTextView.text.toString()) }
        }
    }

    fun setOnAddBtnClickListener(onAddBtnClick: (String, Int) -> Unit) {
        this.onAddBtnClick = onAddBtnClick
    }
}

