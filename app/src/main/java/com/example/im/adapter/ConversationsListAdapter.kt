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
import com.example.im.model.ConversationItem
import com.hyphenate.chat.EMTextMessageBody
import com.hyphenate.util.DateUtils
import java.util.*

class ConversationsListAdapter : ListAdapter<ConversationItem, ConversationsListAdapter.ViewHolder>(object: DiffUtil.ItemCallback<ConversationItem>() {
    override fun areItemsTheSame(oldItem: ConversationItem, newItem: ConversationItem) = oldItem == newItem
    override fun areContentsTheSame(oldItem: ConversationItem, newItem: ConversationItem) = oldItem.targetAccount == newItem.targetAccount
}) {
    private var onItemViewClickListener: OnItemViewClickListener? = null

    fun setOnItemViewClickListener(onItemViewClickListener: OnItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener
    }

    fun setOnItemViewClickListener(onItemViewClick: (Int, ConversationItem) -> Unit) {
        this.onItemViewClickListener = object: OnItemViewClickListener {
            override fun onClick(position: Int, item: ConversationItem) {
                onItemViewClick.invoke(position, item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(parent.context).inflate(R.layout.conversations_list_item, parent, false).let {
            val holder = ViewHolder(it)
            it.setOnClickListener { onItemViewClickListener?.onClick(holder.adapterPosition, getItem(holder.adapterPosition)) }
            holder
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatarImageView by lazy { itemView.findViewById<ImageView>(R.id.avatarImageView) }
        private val accountTextView by lazy { itemView.findViewById<TextView>(R.id.accountTextView) }
        private val lastMessageTextView by lazy { itemView.findViewById<TextView>(R.id.lastMessageTextView) }
        private val timeTextView by lazy { itemView.findViewById<TextView>(R.id.timeTextView) }
        private val unreadCountTextView by lazy { itemView.findViewById<TextView>(R.id.unreadCountTextView) }
        fun bind(itemData: ConversationItem) {
            accountTextView.text = itemData.targetAccount
            itemData.conversation.lastMessage?.let {
                lastMessageTextView.text = if(it.body is EMTextMessageBody) { // it.type 通过type字段判断是否是文本消息也行
                    (it.body as EMTextMessageBody).message
                } else {
                    itemView.context.getString(R.string.non_text_message)
                }
                timeTextView.text = DateUtils.getTimestampString(Date(it.msgTime))
            }
            val count = itemData.conversation.unreadMsgCount
            if(count > 0) {
                unreadCountTextView.text = count.toString()
                unreadCountTextView.visibility = View.VISIBLE
            } else {
                unreadCountTextView.visibility = View.GONE
            }
        }
    }

    interface OnItemViewClickListener {
        fun onClick(position: Int, item: ConversationItem)
    }
    
}

