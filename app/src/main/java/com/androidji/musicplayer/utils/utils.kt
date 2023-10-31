package com.androidji.musicplayer.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

class utils {
    companion object {
        fun dpToPx(dp: Float): Float {
            return dp * Resources.getSystem().displayMetrics.density
        }

        fun replaceFragment(activity: FragmentActivity,id : Int, fragment :Fragment) {
            activity.supportFragmentManager.beginTransaction()
                .replace(id, fragment)
                .commit()
        }
    }
}