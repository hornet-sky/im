package com.example.im.adapter

import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMMessage

open class EMMessageListenerAdapter : EMMessageListener {
    override fun onMessageReceived(msgs: MutableList<EMMessage>?) {
    }
    override fun onCmdMessageReceived(msgs: MutableList<EMMessage>?) {
    }
    override fun onMessageRead(msgs: MutableList<EMMessage>?) {
    }
    override fun onMessageDelivered(msgs: MutableList<EMMessage>?) {
    }
    override fun onMessageRecalled(msgs: MutableList<EMMessage>?) {
    }
    override fun onMessageChanged(msg: EMMessage?, p1: Any?) {
    }
}