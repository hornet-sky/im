package com.example.im.ui.fragment

import android.view.View
import android.widget.Button
import com.example.im.R
import com.example.im.contract.DynamicsContract
import com.example.im.presenter.DynamicsPresenter
import com.example.im.ui.activity.LoginActivity
import com.example.im.utils.LogUtils
import com.hyphenate.chat.EMClient
import kotlinx.android.synthetic.main.fragment_dynamics.*

class DynamicsFragment : BaseFragment(), DynamicsContract.View {
    override val presenter by lazy { DynamicsPresenter(this) }

    override fun getResId() = R.layout.fragment_dynamics
    override fun getTitle() = getString(R.string.dynamics_title)
    override fun initView(view: View?): View {
        view!!.findViewById<Button>(R.id.logoutButton).text = getString(R.string.logout_btn_text, EMClient.getInstance().currentUser)
        return view
    }
    override fun initListener() {
        logoutButton.setOnClickListener {
            presenter.logout()
        }
    }

    override fun onStartLogout() {
        showProgress(getString(R.string.logouting))
    }

    override fun onLogoutSuccess() {
        dismissProgress()
        startActivityThenFinish<LoginActivity>("account" to EMClient.getInstance().currentUser)
    }

    override fun onLogoutFailed(code: Int, message: String?) {
        LogUtils.d("onLogoutFailed [ code = $code, message = $message ]")
        dismissProgress()
        toast(getString(R.string.logout_failed))
    }
}