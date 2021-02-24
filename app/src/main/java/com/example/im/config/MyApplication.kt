package com.example.im.config

import android.app.ActivityManager
import android.app.Application
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import cn.bmob.v3.Bmob
import com.example.im.BuildConfig
import com.example.im.R
import com.example.im.adapter.EMMessageListenerAdapter
import com.example.im.utils.LogUtils
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMOptions

class MyApplication : Application() {
    companion object {
        lateinit var instance: MyApplication
    }
    private val emMessageListener = object: EMMessageListenerAdapter() {
        override fun onMessageReceived(msgs: MutableList<EMMessage>?) {
            LogUtils.d("onMessageReceived [ isForeground = ${isForeground()} ]")
            val currVolume = getCurrentVolume()
            if(isForeground()) { // 进程在前台
                soundPool.play(duanId, currVolume, currVolume, 1, 0, 1F)
            } else { // 进程在后台
                    soundPool.play(yuluId, currVolume, currVolume, 1, 0, 1F)
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
}