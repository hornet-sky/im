package com.example.im.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.im.contract.BasePresenter
import java.io.Serializable

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        const val DELAY: Long = 2000
    }
    protected val handler = Handler(Looper.getMainLooper())
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
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun showSoftKeyBoard(view: View) {
        view.requestFocus()
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

    inline fun <reified T> startActivity(vararg extras: Pair<String, Any>) {
        val intent =  Intent(this, T::class.java).apply {
            fillIntentArguments(this, extras)
        }
        startActivity(intent)
    }
    inline fun <reified T> startActivityThenFinish(vararg extras: Pair<String, Any?>) {
        val intent =  Intent(this, T::class.java).apply {
            fillIntentArguments(this, extras)
        }
        startActivity(intent)
        finish()
    }

    fun fillIntentArguments(intent: Intent, params: Array<out Pair<String, Any?>>) {
        for((key, value) in params) {
            when (value) {
                null -> intent.putExtra(key, null as Serializable?)
                is Int -> intent.putExtra(key, value)
                is Long -> intent.putExtra(key, value)
                is CharSequence -> intent.putExtra(key, value)
                is String -> intent.putExtra(key, value)
                is Float -> intent.putExtra(key, value)
                is Double -> intent.putExtra(key, value)
                is Char -> intent.putExtra(key, value)
                is Short -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Serializable -> intent.putExtra(key, value)
                is Bundle -> intent.putExtra(key, value)
                is Parcelable -> intent.putExtra(key, value)
                is Array<*> -> when {
                    value.isArrayOf<CharSequence>() -> intent.putExtra(key, value)
                    value.isArrayOf<String>() -> intent.putExtra(key, value)
                    value.isArrayOf<Parcelable>() -> intent.putExtra(key, value)
                    else -> throw RuntimeException("Intent extra ${key} has wrong type ${value.javaClass.name}")
                }
                is IntArray -> intent.putExtra(key, value)
                is LongArray -> intent.putExtra(key, value)
                is FloatArray -> intent.putExtra(key, value)
                is DoubleArray -> intent.putExtra(key, value)
                is CharArray -> intent.putExtra(key, value)
                is ShortArray -> intent.putExtra(key, value)
                is BooleanArray -> intent.putExtra(key, value)
                else -> throw RuntimeException("Intent extra ${key} has wrong type ${value.javaClass.name}")
            }
        }
    }
}
