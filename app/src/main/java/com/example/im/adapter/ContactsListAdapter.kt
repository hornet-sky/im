package com.example.im.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.im.R
import com.example.im.model.ContactsItem

class ContactsListAdapter : ListAdapter<ContactsItem, ContactsListAdapter.ViewHolder>(object: DiffUtil.ItemCallback<ContactsItem>() {
    override fun areItemsTheSame(oldItem: ContactsItem, newItem: ContactsItem) = oldItem == newItem
    override fun areContentsTheSame(oldItem: ContactsItem, newItem: ContactsItem) = oldItem.account == newItem.account
}) {
    private var onItemViewClickListener: OnItemViewClickListener? = null
    private var onItemViewLongClickListener: OnItemViewLongClickListener? = null

    fun setOnItemViewClickListener(onItemViewClickListener: OnItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener
    }

    fun setOnItemViewClickListener(onItemViewClick: (Int, ContactsItem) -> Unit) {
        this.onItemViewClickListener = object: OnItemViewClickListener {
            override fun onClick(position: Int, item: ContactsItem) {
                onItemViewClick.invoke(position, item)
            }
        }
    }

    fun setOnItemViewLongClickListener(onItemViewLongClickListener: OnItemViewLongClickListener) {
        this.onItemViewLongClickListener = onItemViewLongClickListener
    }

    fun setOnItemViewLongClickListener(onItemViewLongClick: (Int, ContactsItem) -> Unit) {
        this.onItemViewLongClickListener = object: OnItemViewLongClickListener {
            override fun onLongClick(position: Int, item: ContactsItem) {
                onItemViewLongClick.invoke(position, item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(parent.context).inflate(R.layout.contacts_list_item, parent, false).let {
            val holder = ViewHolder(it)
            it.setOnClickListener { onItemViewClickListener?.onClick(holder.adapterPosition, getItem(holder.adapterPosition)) }
            it.setOnLongClickListener {
                onItemViewLongClickListener?.onLongClick(holder.adapterPosition, getItem(holder.adapterPosition))
                true
            }
            holder
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        val firstLetterVisibility = if(position == 0 || currentItem.firstLetter != getItem(position - 1).firstLetter) View.VISIBLE
            else View.GONE
        holder.bind(currentItem, firstLetterVisibility)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val firstLetterTextView by lazy { itemView.findViewById<TextView>(R.id.firstLetterTextView) }
        private val avatarImageView by lazy { itemView.findViewById<ImageView>(R.id.avatarImageView) }
        private val accountTextView by lazy { itemView.findViewById<TextView>(R.id.accountTextView) }
        fun bind(itemData: ContactsItem, firstLetterVisibility: Int) {
            firstLetterTextView.visibility = firstLetterVisibility
            if(firstLetterVisibility == View.VISIBLE) {
                firstLetterTextView.text = itemData.firstLetter
            }
            accountTextView.text = itemData.account
        }
    }

    interface OnItemViewClickListener {
        fun onClick(position: Int, item: ContactsItem)
    }

    interface OnItemViewLongClickListener {
        fun onLongClick(position: Int, item: ContactsItem)
    }
}

