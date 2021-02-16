package com.example.im.ui.activity

import android.os.Handler
import android.os.Looper
import com.example.im.R
import com.example.im.contract.SplashContract
import com.example.im.presenter.SplashPresenter

class SplashActivity : BaseActivity(), SplashContract.View {
    companion object {
        const val DELAY: Long = 2000
    }
    private val presenter by lazy { SplashPresenter(this) }
    private val handler = Handler(Looper.myLooper()!!)
    private val run = {
        startActivity<LoginActivity>()
        finish()
    }

    override fun init() {
        presenter.checkLoginStatus()
    }

    override fun getLayoutResID() = R.layout.activity_splash

    override fun onNotLoggedIn() {
        handler.postDelayed(run, DELAY)
    }

    override fun onLoggedIn() {
        startActivity<MainActivity>()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(run)
        presenter.destroy()
    }
}