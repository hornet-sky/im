package com.example.im.ui.activity

import com.example.im.R
import com.example.im.contract.RegisterContract
import com.example.im.presenter.RegisterPresenter
import com.example.im.utils.LogUtils
import com.hyphenate.EMError
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.accountEditText
import kotlinx.android.synthetic.main.activity_register.passwordEditText

class RegisterActivity : BaseActivity(), RegisterContract.View {
    override val presenter by lazy { RegisterPresenter(this) }

    override fun getLayoutResID() = R.layout.activity_register

    override fun init() {
        registerButton.setOnClickListener {
            register()
        }
        // 点软键盘上的按钮也能登录
        passwordConfirmEditText.setOnEditorActionListener { v, actionId, event ->
            register()
            return@setOnEditorActionListener true
        }

    }

    private fun register() {
        hideSoftKeyboard()
        presenter.register(accountEditText.text.trim().toString(), passwordEditText.text.trim().toString(), passwordConfirmEditText.text.trim().toString())
    }

    override fun onAccountError() {
        onInputError(accountEditText, R.string.account_error)
    }

    override fun onPasswordError() {
        onInputError(passwordEditText, R.string.password_error)
    }

    override fun onPasswordConfirmError() {
        onInputError(passwordConfirmEditText, R.string.password_confirm_error)
    }

    override fun onStartRegister() {
        showProgress(getString(R.string.registering))
    }

    override fun onRegisterSuccess(account: String) {
        dismissProgress()
        finishForResult(RESULT_CODE_REGISTER_SUCCESS, "account" to account)
        toast(getString(R.string.register_success))
    }

    override fun onRegisterFailed(serverType: Int, code: Int, message: String?) {
        LogUtils.d("onRegisterFailed [ serverType = $serverType, code = $code, message = $message ]")
        dismissProgress()
        val message = if(serverType == RegisterContract.SERVER_TYPE_BMOB) {
            when(code) {
                202 -> getString(R.string.register_failed_account_exists)
                else -> getString(R.string.register_failed)
            }
        } else {
            when(code) {
                EMError.USER_ALREADY_EXIST -> getString(R.string.register_failed_account_exists)
                else -> getString(R.string.register_failed)
            }
        }
        toast(message)
    }

}