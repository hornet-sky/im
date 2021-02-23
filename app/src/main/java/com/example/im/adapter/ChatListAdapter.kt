package com.example.im.adapter

import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.im.R
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody
import com.hyphenate.util.DateUtils
import java.util.*

class ChatListAdapter : ListAdapter<EMMessage, RecyclerView.ViewHolder>(object: DiffUtil.ItemCallback<EMMessage>() {
    override fun areItemsTheSame(oldItem: EMMessage, newItem: EMMessage) = oldItem == newItem
    override fun areContentsTheSame(oldItem: EMMessage, newItem: EMMessage) = oldItem.msgId == newItem.msgId
}) {
    private var loading: Boolean = false
    fun setLoading(loading: Boolean) {
         this.loading = loading
    }

    override fun getItemCount(): Int {
        return if(loading) currentList.size + 1 else currentList.size
    }
    fun getAmendedPosition(position: Int): Int {
        return if(loading) position - 1 else position
    }
    override fun getItemViewType(position: Int): Int {
        return if(loading && position == 0) R.layout.loading_item
        else if(getItem(getAmendedPosition(position)).direct() == EMMessage.Direct.SEND)
            R.layout.chat_send_message_item
        else R.layout.chat_receive_message_item
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LayoutInflater.from(parent.context).inflate(viewType, parent, false).let {
            when(viewType) {
                R.layout.loading_item -> object: RecyclerView.ViewHolder(it) {}
                R.layout.chat_send_message_item -> SendMessageViewHolder(it)
                else -> ReceiveMessageViewHolder(it)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is BaseViewHolder) { // 也可以用loading 和 position 进行判断
            val amendedPosition = getAmendedPosition(position)
            val currentItem = getItem(amendedPosition)
            val timeVisibility = if(amendedPosition == 0 || !DateUtils.isCloseEnough(currentItem.msgTime, getItem(amendedPosition - 1).msgTime)) View.VISIBLE
            else View.GONE
            holder.bind(currentItem, timeVisibility)
        }
    }

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract val timeTextView: TextView
        abstract val messageTextView: TextView
        private fun getFormatTime(milliseconds: Long): String {
            return DateUtils.getTimestampString(Date(milliseconds))
        }
        private fun getMessage(emMsg: EMMessage): String {
            return if(emMsg.type == EMMessage.Type.TXT) (emMsg.body as EMTextMessageBody).message else itemView.context.getString(R.string.non_text_message)
        }
        open fun bind(emMsg: EMMessage, timeVisibility: Int) {
            timeTextView.visibility = timeVisibility
            if(timeVisibility == View.VISIBLE) {
                timeTextView.text = getFormatTime(emMsg.msgTime)
            }
            messageTextView.text = getMessage(emMsg)
        }
    }

    class SendMessageViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override val timeTextView by lazy { itemView.findViewById<TextView>(R.id.timeTextView) }
        val avatarImageView by lazy { itemView.findViewById<ImageView>(R.id.avatarImageView) }
        val progressImageView by lazy { itemView.findViewById<ImageView>(R.id.progressImageView) }
        override val messageTextView by lazy { itemView.findViewById<TextView>(R.id.messageTextView) }
        override fun bind(emMsg: EMMessage, timeVisibility: Int) {
            super.bind(emMsg, timeVisibility)
            when(emMsg.status()) {
                EMMessage.Status.INPROGRESS -> {
                    with(progressImageView) {
                        setImageResource(R.drawable.send_message_progress)
                        visibility = View.VISIBLE
                        (drawable as AnimationDrawable).start()
                    }
                }
                EMMessage.Status.SUCCESS -> {
                    progressImageView.visibility = View.GONE
                }
                EMMessage.Status.FAIL -> {
                    with(progressImageView) {
                        setImageResource(R.mipmap.msg_error)
                        visibility = View.VISIBLE
                    }
                }
                else -> {}
            }
        }
    }

    class ReceiveMessageViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override val timeTextView by lazy { itemView.findViewById<TextView>(R.id.timeTextView) }
        val avatarImageView by lazy { itemView.findViewById<ImageView>(R.id.avatarImageView) }
        override val messageTextView by lazy { itemView.findViewById<TextView>(R.id.messageTextView) }
    }
}

