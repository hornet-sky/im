package com.example.im.contract

import android.widget.EditText
import com.example.im.ui.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_register.*

interface BaseView {
    fun onInputError(editText: EditText, errorMsgResId: Int) {
        editText.error = editText.context.getString(errorMsgResId)
        if(this is BaseActivity) {
            showSoftKeyBoard(editText)
        }
    }
}