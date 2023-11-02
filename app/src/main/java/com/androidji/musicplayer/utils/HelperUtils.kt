package com.androidji.musicplayer.utils

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HelperUtils {
    companion object {
        fun replaceFragment(activity: FragmentActivity,id : Int, fragment :Fragment) {
            activity.supportFragmentManager.beginTransaction()
                .replace(id, fragment)
                .commit()
        }

        fun giveHapticFeedback(context: Context) {
            CoroutineScope(Dispatchers.Main).launch {
                val vibrator = getSystemService(context, Vibrator::class.java)
                vibrator?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val effect = VibrationEffect.createOneShot(
                            80,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                        it.vibrate(effect)
                    } else {
                        // For pre-Oreo
                        it.vibrate(80)
                    }
                }
            }
        }
    }
}