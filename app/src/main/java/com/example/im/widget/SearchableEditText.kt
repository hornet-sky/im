package com.example.im.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.im.R

class SearchableEditText : androidx.appcompat.widget.AppCompatEditText {
    private var endIcon: Drawable? = null
    private var onSearchIconClick: ((String) -> Unit)? = null

    constructor(context: Context?) : super(context) {
        prepareEndIcon(null)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        prepareEndIcon(attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        prepareEndIcon(attrs)
    }

    private fun prepareEndIcon(attrs: AttributeSet?) {
        if(attrs == null) {
            endIcon = resources.getDrawable(R.drawable.selector_icon_btn_search, null)
            return
        }
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.SearchableEditText)
        endIcon = styledAttrs.getDrawable(R.styleable.SearchableEditText_end_icon)
        styledAttrs.recycle()
        if(endIcon == null) {
            endIcon = resources.getDrawable(R.drawable.selector_icon_btn_search, null)
        }
        setCompoundDrawablesWithIntrinsicBounds(null, null,
            endIcon, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        endIcon?.let {
            if(event.action == MotionEvent.ACTION_UP) {
                if(event.x > width - it.intrinsicWidth - 10 // 多减一些,让触发的区域变大
                    && event.y > (height - it.intrinsicHeight) / 2
                    && event.y < (height + it.intrinsicHeight) / 2) {
                    onSearchIconClick?.invoke(text.toString())
                    clearFocus()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun setOnSearchListener(onSearchIconClick: (String) -> Unit) {
        this.onSearchIconClick = onSearchIconClick
    }
}