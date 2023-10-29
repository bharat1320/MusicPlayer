package com.androidji.musicplayer.ui.fragments

import android.app.Activity
import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidji.musicplayer.R
import com.androidji.musicplayer.databinding.FragmentSongPlayerBinding
import com.androidji.musicplayer.databinding.FragmentSongsViewPagerBinding
import com.androidji.musicplayer.ui.adapters.RvSongPlayerAdapter
import com.androidji.musicplayer.ui.viewModels.MainViewModel

class SongPlayerFragment : Fragment() {
    lateinit var binding: FragmentSongPlayerBinding
    lateinit var vm : MainViewModel
    lateinit var adapter : RvSongPlayerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongPlayerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        init()

        observer()

    }

    private fun init() {
        adapter = RvSongPlayerAdapter(requireContext(), arrayListOf())

        binding.rvSongs.let {
            it.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            it.adapter = adapter
        }

//        binding.layout.setTransitionListener(object : MotionLayout.TransitionListener {
//            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
//            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}
//            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}
//            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
//        })
    }

    private fun observer() {
        vm.currentSongId.observe(requireActivity()) { idd ->
            binding.rvSongs.smoothScrollToPosition(idd)
            val currentSong = adapter.songs.filter { it.id == idd }[0]
            binding.itemSongName.text = currentSong.name
            binding.itemSongSinger.text = currentSong.artist
        }

        vm.stateOpened.observe(requireActivity()) {
            if(it) {
                binding.itemSongSinger.visibility = View.GONE
                binding.itemSongName.textSize = 18f
                binding.layout.transitionToEnd()
            } else {
                binding.itemSongName.textSize = 22f
                binding.itemSongSinger.visibility = View.VISIBLE
                binding.layout.transitionToStart()
            }
        }

        vm.songsList.observe(requireActivity()) {
            adapter.refreshData(it.data)
        }
    }
}