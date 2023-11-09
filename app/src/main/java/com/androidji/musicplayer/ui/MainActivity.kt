package com.androidji.musicplayer.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.androidji.musicplayer.R
import com.androidji.musicplayer.data.ViewPagerFragment
import com.androidji.musicplayer.databinding.ActivityMainBinding
import com.androidji.musicplayer.ui.fragments.SongPlayerFragment
import com.androidji.musicplayer.ui.fragments.HomeViewPagerFragment
import com.androidji.musicplayer.ui.fragments.SongsViewPagerFragment
import com.androidji.musicplayer.ui.viewModels.MainViewModel
import com.androidji.musicplayer.utils.HelperUtils
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var vm : MainViewModel
    lateinit var playerFragment: SongPlayerFragment
    lateinit var pageChangeCallback  : ViewPager2.OnPageChangeCallback
    var fragments = arrayListOf<ViewPagerFragment>()
    var fragmentStateOpen = false

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
                    HelperUtils.giveHapticFeedback(this@MainActivity)
                } else {
                    binding.tabForYou.setTextColor(resources.getColor(R.color.white_50,null))
                    binding.tabForYouDot.visibility = View.GONE
                    binding.tabTopTracks.setTextColor(resources.getColor(R.color.white,null))
                    binding.tabTopTracksDot.visibility = View.VISIBLE
                    HelperUtils.giveHapticFeedback(this@MainActivity)
                }
            }
        })
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    private fun apiCalls() {
        vm.repository.getSongList()
    }

    private fun observer() {
        vm.currentSong.observe(this) {
            fragmentStateOpen = true
            binding.fragmentView.visibility = View.VISIBLE
            HelperUtils.replaceFragment(this,binding.fragmentView.id, playerFragment)
            binding.tabLayout.setBackgroundResource(R.color.black)
        }

        vm.currentSongPlayerState.observe(this) {
            binding.fragmentSongPlayerConstraint.visibility = View.VISIBLE
            Glide.with(this).load(it.songCover).circleCrop().into(binding.songImage)
            binding.songName.text = it.songName
            binding.fragmentSongPlayerConstraint.background = it.songLayoutBg
            Glide.with(this).load(R.drawable.play_to_pause).into(binding.songPlayButton)
        }

        vm.currentSongPlayingState.observe(this) {
            Glide.with(this)
                .load(if (it) R.drawable.play_to_pause else R.drawable.pause_to_play)
                .into(binding.songPlayButton)
        }
    }

    private fun listeners() {
        binding.tabForYouConstraint.setOnClickListener {
            binding.viewPager.currentItem = 0
        }

        binding.tabTopTracksConstraint.setOnClickListener {
            binding.viewPager.currentItem = 1
        }

        binding.songPlayButton.setOnClickListener {
            vm.currentSongPlayingState.postValue(!(vm.currentSongPlayingState.value ?: false))
        }
    }

    override fun onDestroy() {
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroy()
    }

    override fun onBackPressed() {
//        if(fragmentStateOpen) {
//            fragmentStateOpen = false
//
//        } else {
            super.onBackPressed()
//        }
    }
}