package com.androidji.musicplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidji.musicplayer.data.CurrentSong
import com.androidji.musicplayer.databinding.FragmentSongsViewPagerBinding
import com.androidji.musicplayer.ui.adapters.RvSongsAdapter
import com.androidji.musicplayer.ui.viewModels.MainViewModel

class SongsViewPagerFragment : Fragment() {
    lateinit var binding: FragmentSongsViewPagerBinding
    lateinit var vm : MainViewModel
    lateinit var songsRvAdapter : RvSongsAdapter
    private var isTopTracks: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isTopTracks = it.getBoolean(IS_TOP_TRACKS,false)
        }
    }

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

    private fun init() {
        songsRvAdapter = RvSongsAdapter(requireContext(), arrayListOf()) {
            vm.currentSong.postValue(it)
        }
        binding.rvSongList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = songsRvAdapter
        }
    }

    private fun observer() {
        vm.songsList.observe(requireActivity()) {
            if(isTopTracks == true) {
                it.data.filter { it.topTrack == true }
            }
            songsRvAdapter.refreshData(it.data)
        }
    }

    companion object {
        private const val IS_TOP_TRACKS = "IS_TOP_TRACKS"
        @JvmStatic
        fun newInstance(isTopTracks: Boolean) =
            SongsViewPagerFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_TOP_TRACKS, isTopTracks)
                }
            }
    }
}