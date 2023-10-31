package com.androidji.musicplayer.ui

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.androidji.musicplayer.data.ViewPagerFragment
import com.androidji.musicplayer.databinding.ActivityMainBinding
import com.androidji.musicplayer.ui.fragments.SongPlayerFragment
import com.androidji.musicplayer.ui.fragments.SongsRecyclerViewFragment
import com.androidji.musicplayer.ui.viewModels.MainViewModel
import com.androidji.musicplayer.utils.utils
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var vm : MainViewModel
    lateinit var playerFragment: Fragment
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
        fragments.add(ViewPagerFragment(SongsRecyclerViewFragment.newInstance(false),"For You"))
        fragments.add(ViewPagerFragment(SongsRecyclerViewFragment.newInstance(true),"Top Tracks"))
        binding.viewPager.adapter = (object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int): Fragment {
                return fragments[position].fragment
            }
        })

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = Html.fromHtml("<b>${fragments[position].name}</b>")
        }.attach()

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