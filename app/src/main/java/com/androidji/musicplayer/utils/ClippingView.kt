package com.androidji.musicplayer.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.widget.FrameLayout


class ClippingView : FrameLayout {
    var customCornerRadius = 0f

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    )

    override fun dispatchDraw(canvas: Canvas) {
        val save = canvas.save()
        val path = Path()
        path.addRoundRect(0f, 0f, width * 1f, height * 1f, customCornerRadius, customCornerRadius, Path.Direction.CW)
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(save)
    }

    fun setCornerRadius(radius: Float) {
        customCornerRadius = radius
        invalidate()
    }

    fun getCornerRadius(): Float {
        return customCornerRadius
    }
}
