package com.example.im.ui.fragment
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
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
        }
        return initView(view)
    }

    protected open fun getResId(): Int? {
        return null
    }

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
}