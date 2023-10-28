package com.androidji.musicplayer.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidji.musicplayer.databinding.ActivityMainBinding
import com.androidji.musicplayer.ui.adapters.RvSongsAdapter
import com.androidji.musicplayer.ui.viewModels.MainViewModel

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var vm : MainViewModel
    lateinit var songsRvAdapter : RvSongsAdapter
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
        songsRvAdapter = RvSongsAdapter(this, arrayListOf())
        binding.rvSongList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = songsRvAdapter
        }
    }

    private fun apiCalls() {
        vm.repository.getSongList()
    }

    private fun observer() {
        vm.songsList.observe(this) {
            songsRvAdapter.refreshData(it.data)
        }
    }

    private fun listeners() {
        binding.motionLayout.transitionToEnd()
        binding.buttonPlay.setOnClickListener {
            Toast.makeText(this, stateExpanded.toString(), Toast.LENGTH_SHORT).show()
            if(stateExpanded) {
                binding.motionLayout.transitionToEnd()
            } else {
                binding.motionLayout.transitionToStart()
            }
            stateExpanded = !stateExpanded
        }
        binding.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                // Handle transition start
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                // Handle transition change
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                // Handle transition complete
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                // Handle transition trigger
            }
        })
    }
}