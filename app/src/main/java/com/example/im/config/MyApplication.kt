package com.example.im.config

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cn.bmob.v3.Bmob
import com.example.im.BuildConfig
import com.example.im.R
import com.example.im.adapter.EMMessageListenerAdapter
import com.example.im.ui.activity.ChatActivity
import com.example.im.utils.LogUtils
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMOptions
import com.hyphenate.chat.EMTextMessageBody

class MyApplication : Application() {
    companion object {
        lateinit var instance: MyApplication
        private val NOTIFI_AUDIO_PLAYER_CHANNEL_ID = "channelId_01"
        private val NOTIFI_AUDIO_PLAYER_CHANNEL_NAME = "channelName_01"
        private val NOTIFI_AUDIO_PLAYER_ID = 1001
    }
    private val emMessageListener = object: EMMessageListenerAdapter() {
        override fun onMessageReceived(msgs: MutableList<EMMessage>?) {
            LogUtils.d("onMessageReceived [ isForeground = ${isForeground()} ]")
            val currVolume = getCurrentVolume()
            if(isForeground()) { // 进程在前台
                soundPool.play(duanId, currVolume, currVolume, 1, 0, 1F)
            } else { // 进程在后台
                soundPool.play(yuluId, currVolume, currVolume, 1, 0, 1F)
                if(!msgs.isNullOrEmpty()) {
                    showNotification(msgs.last())
                }
            }
        }
    }
    private fun getCurrentVolume(): Float {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        val streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        LogUtils.d("getCurrentVolume [ streamVolume = $streamVolume, streamMaxVolume = $streamMaxVolume ]")
        return 1.0F * streamVolume / streamMaxVolume
    }
    // 5.0 以前
    // private val soundPool: SoundPool = SoundPool(2, AudioManager.STREAM_MUSIC, 0)
    /* 5.0 及 之后 */
    private val soundPool: SoundPool = SoundPool.Builder()
            .setMaxStreams(16)
            .setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
            .build()

    private var duanId: Int = -1 // 不能通过lazy加载，必须在使用前加载好，不然没声音
    private var yuluId: Int = -1

    init {
        instance = this
    }
    override fun onCreate() {
        super.onCreate()
        // 1、Bmob云后端 初始化
        Bmob.initialize(this, "c18f4500a5799a9b5584276f82d7fce4")

        // 2、环信 初始化
        val options = EMOptions()
        // 默认添加好友时，是不需要验证的，改成需要验证
        // options.setAcceptInvitationAlways(false)
        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.autoTransferMessageAttachments = true
        // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true)

        //初始化
        EMClient.getInstance().init(applicationContext, options)
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(BuildConfig.DEBUG)
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener)

        soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
            LogUtils.d("soundPool.setOnLoadCompleteListener [ sampleId = $sampleId, status = $status ]") // [ sampleId = 1, status = 0 ]
        }

        duanId = soundPool.load(this, R.raw.duan, 1)
        yuluId = soundPool.load(this, R.raw.yulu, 1)
    }

    private fun isForeground(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for(runningAppProcess in activityManager.runningAppProcesses) {
            if(runningAppProcess.processName == packageName) {
                return runningAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
        }
        return false
    }

    private fun showNotification(msg: EMMessage) {
        val msgText = if(msg.type == EMMessage.Type.TXT) {
            (msg.body as EMTextMessageBody).message
        } else {
            getString(R.string.non_text_message)
        }
        updateNotificationUI(msgText)
    }

    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManagerCompat? = null
    private fun updateNotificationUI(content: String) {
        if(notificationBuilder == null) {
            val channelId = createNotificationChannel(NOTIFI_AUDIO_PLAYER_CHANNEL_ID,
                    NOTIFI_AUDIO_PLAYER_CHANNEL_NAME,
                    NotificationManagerCompat.IMPORTANCE_LOW) // IMPORTANCE_DEFAULT 有提示音
            notificationBuilder = getNotificationBuilder(channelId)
        }
        if(notificationManager == null) {
            notificationManager = NotificationManagerCompat.from(this)
        }
        notificationManager!!.notify(NOTIFI_AUDIO_PLAYER_ID, notificationBuilder!!.setContentText(content).build())
    }

    private fun getNotificationBuilder(channelId: String?): NotificationCompat.Builder {
        return if(channelId == null) NotificationCompat.Builder(this)
        else NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_chat_bubble_24)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.avatar1))
                .setContentTitle(getString(R.string.receive_new_message))
                //.setContentIntent(getPendingIntentBody())
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_LOW) // 不要提示音
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setOngoing(true)
    }

    private fun createNotificationChannel(channelId: String, channelName: String, level: Int): String? {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) { // android 8 及以上版本才有频道这个概念
            return null
        }
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(NotificationChannel(channelId, channelName, level))
        return channelId
    }

    private fun getPendingIntentBody(targetAccount: String): PendingIntent {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("targetAccount", targetAccount)
        return TaskStackBuilder.create(this).let {
            it.addNextIntentWithParentStack(intent)
            it.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}