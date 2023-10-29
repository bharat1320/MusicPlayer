package com.androidji.musicplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidji.musicplayer.databinding.FragmentSongPlayerBinding
import com.androidji.musicplayer.ui.adapters.RvSongPlayerAdapter
import com.androidji.musicplayer.ui.viewModels.MainViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation

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
        vm.currentSong.observe(requireActivity()) { it ->
            binding.rvSongs.smoothScrollToPosition(it.position)
            binding.itemSongName.text = it.song.name
            binding.itemSongSinger.text = it.song.artist
            Glide.with(requireContext())
                .asBitmap()
                .load(it.song.getImageUrl())
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 12)))
                .into(binding.background)
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