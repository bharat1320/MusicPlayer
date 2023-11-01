package com.androidji.musicplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.androidji.musicplayer.R
import com.androidji.musicplayer.data.CurrentSong
import com.androidji.musicplayer.databinding.FragmentSongsViewPagerBinding
import com.androidji.musicplayer.ui.adapters.RvSongPlayerAdapter
import com.androidji.musicplayer.ui.viewModels.MainViewModel

class SongsViewPagerFragment : Fragment() {
    lateinit var binding: FragmentSongsViewPagerBinding
    lateinit var songsAdapter : RvSongPlayerAdapter
    lateinit var vm : MainViewModel
    lateinit var pageChangeCallback  : OnPageChangeCallback
    var skipCurrentSongCallback = false
    val pageMarginPx by lazy { resources.getDimensionPixelOffset(R.dimen.pageMargin) }
    val offsetPx  by lazy { resources.getDimensionPixelOffset(R.dimen.offset) }
    var newOffset = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongsViewPagerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        init()

        observer()
    }

    fun init() {
        pageChangeCallback = (object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                vm.songsList.value?.data?.get(position)?.let {
                    skipCurrentSongCallback = true
                    vm.currentSong.postValue(CurrentSong(it, position))
                }
            }
        })
        binding.rvSongs.registerOnPageChangeCallback(pageChangeCallback)

        songsAdapter = RvSongPlayerAdapter(requireContext(), arrayListOf())

        binding.rvSongs.apply {
            adapter = songsAdapter
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 2

            setPadding(offsetPx, 0, offsetPx, 0)
            setPageTransformer(MarginPageTransformer(pageMarginPx))
        }
    }

    fun observer() {
        vm.currentSong.observe(requireActivity()) {
            if(skipCurrentSongCallback) {
                skipCurrentSongCallback = false
            } else {
                binding.rvSongs.currentItem = it.position
            }
        }

        vm.songsList.observe(requireActivity()) {
            songsAdapter.refreshData(it.data)
        }

        vm.animationOnProgress.observe(requireActivity()) {
            newOffset = offsetPx - (offsetPx * it).toInt()
            binding.rvSongs.setPadding(newOffset, 0, newOffset, 0)
            binding.rvSongs.currentItem = vm.currentSong.value?.position ?: 0
        }
    }

    override fun onDestroy() {
        binding.rvSongs.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroy()
    }
}