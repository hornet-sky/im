package com.example.im.adapter

import com.hyphenate.EMCallBack

abstract class EMCallBackAdapter: EMCallBack {
    override fun onSuccess() {

    }
    override fun onError(code: Int, message: String?) {

    }
    override fun onProgress(progress: Int, message: String?) {

    }
}