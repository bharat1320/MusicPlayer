package com.androidji.musicplayer.ui.fragments

import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.androidji.musicplayer.R
import com.androidji.musicplayer.databinding.FragmentSongPlayerBinding
import com.androidji.musicplayer.databinding.FragmentSongsViewPagerBinding
import com.androidji.musicplayer.ui.viewModels.MainViewModel

class SongPlayerFragment : Fragment() {
    lateinit var binding: FragmentSongPlayerBinding
    lateinit var vm : MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongPlayerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        vm.stateOpened.observe(requireActivity()) {
            if(it) {
                binding.layout.transitionToEnd()
            } else {
                binding.layout.transitionToStart()
            }
        }


    }
}