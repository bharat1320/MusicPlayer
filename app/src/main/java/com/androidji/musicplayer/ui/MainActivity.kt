package com.androidji.musicplayer.ui

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.androidji.musicplayer.data.ViewPagerFragment
import com.androidji.musicplayer.databinding.ActivityMainBinding
import com.androidji.musicplayer.ui.fragments.SongPlayerFragment
import com.androidji.musicplayer.ui.fragments.SongsViewPagerFragment
import com.androidji.musicplayer.ui.viewModels.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var vm : MainViewModel
    var playerFragment: Fragment = SongPlayerFragment()
    var fragments = arrayListOf<ViewPagerFragment>()
    var stateClosed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this)[MainViewModel::class.java]

        supportActionBar?.hide()

        observer()

        init()

        apiCalls()

        listeners()

        binding.fragmentSongPlayer.visibility = View.GONE
    }

    private fun init() {
        fragments.add(ViewPagerFragment(SongsViewPagerFragment.newInstance(false),"For You"))
        fragments.add(ViewPagerFragment(SongsViewPagerFragment.newInstance(true),"Top Tracks"))
        binding.viewPager.adapter = (object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int): Fragment {
                return fragments[position].fragment
            }
        })

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = Html.fromHtml("<b>${fragments[position].name}</b>")
        }.attach()

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentSongPlayer.id, playerFragment)
            .commit()

        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                stateClosed = !stateClosed
                vm.stateOpened.postValue(stateClosed)
            }
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })

    }

    private fun apiCalls() {
        vm.repository.getSongList()
    }

    private fun observer() {
        vm.currentSongId.observe(this) {
            openPlayerFragment()
        }
    }

    private fun listeners() {
        binding.fragmentSongPlayer.setOnClickListener {
            openPlayerFragment()
        }
    }

    fun openPlayerFragment() {
        binding.fragmentSongPlayer.visibility = View.VISIBLE
        if(stateClosed) {
            binding.motionLayout.transitionToEnd()
        } else {
            binding.motionLayout.transitionToStart()
        }
    }

    override fun onBackPressed() {
        if(stateClosed) {
            val i = Intent()
            i.action = Intent.ACTION_MAIN
            i.addCategory(Intent.CATEGORY_HOME)
            this.startActivity(i)
        } else {
            openPlayerFragment()
        }
        if(false) {
            // dont close the app, put it in background
            super.onBackPressed()
        }
    }
}