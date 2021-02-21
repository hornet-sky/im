package com.example.im.config

import android.app.Application
import cn.bmob.v3.Bmob
import com.example.im.BuildConfig
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMOptions

class MyApplication : Application() {
    companion object {
        lateinit var instance: MyApplication
    }
    init {
        instance = this
    }
    override fun onCreate() {
        super.onCreate()
        // 1、Bmob云后端 初始化
        Bmob.initialize(this, "c18f4500a5799a9b5584276f82d7fce4");

        // 2、环信 初始化
        val options = EMOptions()
        // 默认添加好友时，是不需要验证的，改成需要验证
        // options.setAcceptInvitationAlways(false);
        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.autoTransferMessageAttachments = true
        // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true)

        //初始化
        EMClient.getInstance().init(applicationContext, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(BuildConfig.DEBUG);
    }
}