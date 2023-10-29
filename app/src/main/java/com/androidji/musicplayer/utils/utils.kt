package com.androidji.musicplayer.utils

import android.content.res.Resources

class utils {
    companion object {
        fun dpToPx(dp: Float): Float {
            return dp * Resources.getSystem().displayMetrics.density
        }
    }
}