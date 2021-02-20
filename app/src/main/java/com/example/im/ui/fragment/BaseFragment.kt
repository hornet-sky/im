package com.example.im.ui.fragment
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.im.R
import com.example.im.contract.BasePresenter
import kotlinx.android.synthetic.main.top_action_bar.view.*
import java.io.Serializable

abstract class BaseFragment : Fragment() {
    private val progressDialog by lazy {
        ProgressDialog(activity)
    }
    protected abstract val presenter: BasePresenter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    protected open fun init() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View? = null
        getResId()?.let {
            view = inflater.inflate(it, container, false)
            initTitle(view)
        }
        return initView(view)
    }

    protected open fun getResId(): Int? {
        return null
    }

    private fun initTitle(view: View?) {
        view?.let {
            (it as ViewGroup).findViewById<TextView>(R.id.titleTextView)?.let {
                it.text = getTitle()
            }
        }
    }

    protected open fun getTitle() = ""

    protected open fun initView(view: View?): View {
        return view ?:
            TextView(context).apply {
                gravity = Gravity.CENTER
                text = this@BaseFragment.javaClass.simpleName
                setTextColor(Color.RED)
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListener()
        initData()
    }

    protected open fun initData() {

    }

    protected open fun initListener() {

    }

    fun toast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showProgress(message: String) {
        progressDialog.setMessage(message)
        progressDialog.show()
    }

    fun dismissProgress() {
        progressDialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
        if(progressDialog.isShowing)
            progressDialog.dismiss()
    }

    inline fun <reified T> startActivity(vararg extras: Pair<String, Any>) {
        val intent =  Intent(activity, T::class.java).apply {
            fillIntentArguments(this, extras)
        }
        startActivity(intent)
    }
    inline fun <reified T> startActivityThenFinish(vararg extras: Pair<String, Any?>) {
        val intent =  Intent(activity, T::class.java).apply {
            fillIntentArguments(this, extras)
        }
        startActivity(intent)
        activity!!.finish()
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