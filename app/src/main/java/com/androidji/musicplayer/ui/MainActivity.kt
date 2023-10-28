package com.androidji.musicplayer.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.window.OnBackInvokedDispatcher
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.androidji.musicplayer.R
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
    var stateExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this)[MainViewModel::class.java]

        supportActionBar?.hide()

        observer()

        init()

        apiCalls()

//        listeners()

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

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentSongPlayer.id, playerFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

//        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
//            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
//                stateExpanded = !stateExpanded
//                vm.stateOpened.postValue(stateExpanded)
//            }
//            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
//            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}
//            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
//        })

    }

    private fun apiCalls() {
        vm.repository.getSongList()
    }

    private fun observer() {
        vm.currentSongId.observe(this) {
            openPlayerFragment()
        }
    }

    fun openPlayerFragment() {
        binding.fragmentSongPlayer.visibility = View.VISIBLE
        if(stateExpanded) {
//            binding.motionLayout.transitionToEnd()
        } else {
//            binding.motionLayout.transitionToStart()
        }
    }

    class NonTouchableMotionLayout(context: Context, attrs: AttributeSet? = null) :
        MotionLayout(context, attrs) {

        override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
            return false
        }
    }


//    private fun listeners() {
//        binding.motionLayout.transitionToEnd()
//        binding.buttonPlay.setOnClickListener {
//            Toast.makeText(this, stateExpanded.toString(), Toast.LENGTH_SHORT).show()
//            if(stateExpanded) {
//                binding.motionLayout.transitionToEnd()
//            } else {
//                binding.motionLayout.transitionToStart()
//            }
//            stateExpanded = !stateExpanded
//        }
}