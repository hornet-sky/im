package com.example.im.ui.activity

import com.example.im.R
import com.example.im.contract.LoginContract
import com.example.im.presenter.LoginPresenter
import com.example.im.utils.LogUtils
import com.hyphenate.EMError
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.accountEditText
import kotlinx.android.synthetic.main.activity_login.passwordEditText
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : BaseActivity(), LoginContract.View {
    override val presenter by lazy { LoginPresenter(this) }
    /* gradle 声明 kotlin-android-extensions 后可以根据组件ID直接获得组件
    private val avatarImageView by lazy { findViewById<ImageView>(R.id.avatarImageView) }
    private val accountEditText by lazy { findViewById<EditText>(R.id.accountEditText) }
    private val passwordEditText by lazy { findViewById<EditText>(R.id.passwordEditText) }
    private val loginButton by lazy { findViewById<Button>(R.id.loginButton) }
    private val registerUserTextView by lazy { findViewById<TextView>(R.id.registerUserTextView) }
    */

    override fun init() {
        intent.getStringExtra("account")?.let {
            accountEditText.setText(it)
        }
        loginButton.setOnClickListener {
            login()
        }
        // 点软键盘上的按钮也能登录
        passwordEditText.setOnEditorActionListener { v, actionId, event ->
            login()
            return@setOnEditorActionListener true
        }
        registerUserTextView.setOnClickListener {
            hideSoftKeyboard()
            startActivityThenFinish<RegisterActivity>()
        }
    }

    private fun login() {
        hideSoftKeyboard()
        presenter.login(accountEditText.text.trim().toString(), passwordEditText.text.trim().toString())
    }

    override fun getLayoutResID() = R.layout.activity_login

    override fun onAccountError() {
        onInputError(accountEditText, R.string.account_error)
    }

    override fun onPasswordError() {
        onInputError(passwordEditText, R.string.password_error)
    }

    override fun onStartLogin() {
        showProgress(getString(R.string.logging))
    }

    override fun onLoggedInSuccess() {
        dismissProgress()
        startActivityThenFinish<MainActivity>()
    }

    override fun onLoggedInFailed(code: Int, message: String?) {
        LogUtils.d("onLoggedInFailed [ code = $code, message = $message ]")
        dismissProgress()
        toast(when(code) {
            EMError.USER_NOT_FOUND -> getString(R.string.login_failed_account_not_found)
            EMError.USER_ALREADY_LOGIN -> getString(R.string.login_failed_account_already_login)
            else -> getString(R.string.login_failed)
        })
    }
}