package com.example.im.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.im.R
import com.example.im.utils.DensityUtils

class ContactsIndexView : View {
    private val letters = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")
    private var viewW = 0
    private var viewH = 0
    private var cellH = 0F
    private var centerX = 0F
    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = DensityUtils.sp2px(context, 16)
            textAlign = Paint.Align.CENTER
        }
    }
    private val baselineH by lazy {
        with(paint.fontMetrics) {
            (cellH - ascent - descent) / 2F // (descent - ascent) / 2F - descent 即 字体高度的一半 减去下坡度descent。其中ascent是负值、descent是正值
        }
    }
    private var onSlidingListener: OnSlidingListener? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        this.viewW = w
        this.viewH = h
        this.centerX = w / 2F
        this.cellH = h.toFloat() / letters.size
    }

    override fun onDraw(canvas: Canvas?) {
        var baselineY: Float
        for((idx, letter) in letters.withIndex()) {
            baselineY = idx * cellH + baselineH
            canvas!!.drawText(letter, centerX, baselineY, paint)
        }
    }

    private var defBg: Drawable? = null
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                if(defBg == null) {
                    defBg = background ?: ColorDrawable(Color.TRANSPARENT)
                }
                background = ColorDrawable(resources.getColor(R.color.half_transparent, null))
                invalidate()
                onSlidingListener?.let {
                    if(event.y <= 0) it.onSliding(letters[0])
                    else if(event.y >= viewH ) it.onSliding(letters[letters.size - 1])
                    else {
                        val idx = (event.y / cellH).toInt()
                        it.onSliding(letters[idx])
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                onSlidingListener?.let {
                    if(event.y <= 0) it.onSliding(letters[0])
                    else if(event.y >= viewH ) it.onSliding(letters[letters.size - 1])
                    else {
                        val idx = (event.y / cellH).toInt()
                        it.onSliding(letters[idx])
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                background = defBg
                invalidate()
                onSlidingListener?.onSlidingRelease()
            }
        }
        return true
    }

    fun setOnSlidingListener(onSlidingListener: OnSlidingListener) {
        this.onSlidingListener = onSlidingListener
    }

    interface OnSlidingListener {
        fun onSliding(letter: String)
        fun onSlidingRelease()
    }
}