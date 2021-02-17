package com.example.im.ui.activity

import com.example.im.R
import com.example.im.contract.MainContract
import com.example.im.presenter.MainPresenter

class MainActivity : BaseActivity(), MainContract.View {
    override val presenter by lazy { MainPresenter(this) }

    override fun getLayoutResID() = R.layout.activity_main

}