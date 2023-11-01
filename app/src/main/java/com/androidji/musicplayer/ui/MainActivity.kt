package com.androidji.musicplayer.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.androidji.musicplayer.R
import com.androidji.musicplayer.data.CurrentSong
import com.androidji.musicplayer.data.ViewPagerFragment
import com.androidji.musicplayer.databinding.ActivityMainBinding
import com.androidji.musicplayer.ui.fragments.SongPlayerFragment
import com.androidji.musicplayer.ui.fragments.HomeViewPagerFragment
import com.androidji.musicplayer.ui.viewModels.MainViewModel
import com.androidji.musicplayer.utils.utils
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var vm : MainViewModel
    lateinit var playerFragment: Fragment
    lateinit var pageChangeCallback  : ViewPager2.OnPageChangeCallback
    var fragments = arrayListOf<ViewPagerFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        observer()

        apiCalls()

        listeners()

    }

    private fun init() {
        vm = ViewModelProvider(this)[MainViewModel::class.java]

        supportActionBar?.hide()

        playerFragment = SongPlayerFragment()
        fragments.add(ViewPagerFragment(HomeViewPagerFragment.newInstance(false),"For You"))
        fragments.add(ViewPagerFragment(HomeViewPagerFragment.newInstance(true),"Top Tracks"))
        binding.viewPager.adapter = (object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int): Fragment {
                return fragments[position].fragment
            }
        })

        pageChangeCallback = (object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(position == 0) {
                    binding.tabForYou.setTextColor(resources.getColor(R.color.white,null))
                    binding.tabForYouDot.visibility = View.VISIBLE
                    binding.tabTopTracks.setTextColor(resources.getColor(R.color.white_50,null))
                    binding.tabTopTracksDot.visibility = View.GONE
                } else {
                    binding.tabForYou.setTextColor(resources.getColor(R.color.white_50,null))
                    binding.tabForYouDot.visibility = View.GONE
                    binding.tabTopTracks.setTextColor(resources.getColor(R.color.white,null))
                    binding.tabTopTracksDot.visibility = View.VISIBLE
                }
            }
        })
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)

        utils.replaceFragment(this,binding.fragmentSongPlayer.id, playerFragment)

        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })

        binding.fragmentSongPlayer.visibility = View.GONE
    }

    private fun apiCalls() {
        vm.repository.getSongList()
    }

    private fun observer() {
        vm.stateOpened.observe(this) {
            openPlayerFragment(it)
        }
    }

    private fun listeners() {
        binding.fragmentSongPlayer.setOnClickListener {
            vm.stateOpened.postValue(true)
        }
    }

    fun openPlayerFragment(stateOpen: Boolean = true) {
        if(stateOpen) {
            binding.motionLayout.transitionToEnd()
        } else {
            binding.motionLayout.transitionToStart()
        }
    }

    override fun onDestroy() {
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if(vm.stateOpened.value == false) {
            val i = Intent()
            i.action = Intent.ACTION_MAIN
            i.addCategory(Intent.CATEGORY_HOME)
            this.startActivity(i)
        } else {
            vm.stateOpened.postValue(false)
        }
        if(false) {
            // dont close the app, put it in background
            super.onBackPressed()
        }
    }
}