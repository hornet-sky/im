package com.example.im.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.im.contract.BasePresenter
import com.example.im.presenter.SplashPresenter

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        const val DELAY: Long = 2000
    }
    protected val handler = Handler(Looper.myLooper()!!)
    protected open var run: (() -> Unit)? = null
    protected abstract val presenter: BasePresenter<*>
    private val inputMethodManager by lazy { getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager }

    private val progressDialog by lazy {
        ProgressDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResID())
        init()
    }

    open fun init() {}

    abstract fun getLayoutResID(): Int

    fun showProgress(message: String) {
        progressDialog.setMessage(message)
        progressDialog.show()
    }

    fun dismissProgress() {
        progressDialog.dismiss()
    }

    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun hideSoftKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    fun showSoftKeyBoard(view: View) {
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    override fun onDestroy() {
        super.onDestroy()
        println("BaseActivity.onDestroy [ run = $run ]")
        run?.let {
            handler.removeCallbacks(it)
        }
        presenter.destroy()
    }

    inline fun <reified T> startActivity() {
        val intent =  Intent(this, T::class.java)
        startActivity(intent)
    }
}
