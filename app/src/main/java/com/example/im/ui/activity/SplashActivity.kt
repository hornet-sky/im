package com.example.im.ui.activity

import com.example.im.R
import com.example.im.contract.SplashContract
import com.example.im.presenter.SplashPresenter

class SplashActivity : BaseActivity(), SplashContract.View {
    override val presenter by lazy { SplashPresenter(this) }
    override var run: (() -> Unit)? = {
        startActivityAndFinish<LoginActivity>()
    }

    override fun init() {
        presenter.checkLoginStatus()
    }

    override fun getLayoutResID() = R.layout.activity_splash

    override fun onNotLoggedIn() {
        handler.postDelayed(run!!, DELAY)
    }

    override fun onLoggedIn() {
        startActivityAndFinish<MainActivity>()
    }

}