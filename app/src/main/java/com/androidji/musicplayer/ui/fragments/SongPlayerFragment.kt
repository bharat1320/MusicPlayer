package com.androidji.musicplayer.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.androidji.musicplayer.R
import com.androidji.musicplayer.data.Song
import com.androidji.musicplayer.databinding.FragmentSongPlayerBinding
import com.androidji.musicplayer.ui.viewModels.MainViewModel
import com.androidji.musicplayer.utils.utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import jp.wasabeef.glide.transformations.BlurTransformation
import java.util.concurrent.TimeUnit

class SongPlayerFragment : Fragment() {
    lateinit var binding: FragmentSongPlayerBinding
    lateinit var vm : MainViewModel
    var isUpdating = false
    var isPlaying = false
    private lateinit var exoPlayer: ExoPlayer
    lateinit var playbackProgressRunnable : Runnable
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private var songsViewPagerFragment = SongsViewPagerFragment()
    }

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

        listeners()

    }

    private fun init() {
        utils.replaceFragment(requireActivity(),binding.fragmentSongs.id, songsViewPagerFragment)

        val extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
        val renderersFactory = DefaultRenderersFactory(requireActivity().applicationContext)
            .setExtensionRendererMode(extensionRendererMode)
            .setEnableDecoderFallback(true)
        val trackSelector = DefaultTrackSelector(requireActivity().applicationContext)

        exoPlayer = ExoPlayer.Builder(requireActivity().applicationContext, renderersFactory)
            .setTrackSelector(trackSelector)
            .build().apply {
                trackSelectionParameters = DefaultTrackSelector.Parameters.Builder(requireActivity().applicationContext).build()
                playWhenReady = false
                addListener(object : Player.Listener {
                    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {}
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {}
                            Player.STATE_READY -> {
                                binding.songSeekbar.progress = 0
                                binding.songEndTimeStamp.text = convertMillisToTime(exoPlayer.duration)
                            }
                            Player.STATE_ENDED -> {
                                binding.songSeekbar.progress = 0
                                exoPlayer.seekTo(0)
                                exoPlayer.playWhenReady = true
                            }
                        }
                    }
                    override fun onPlayerError(error: PlaybackException) {}
                })
            }

        playbackProgressRunnable = Runnable {
            if (exoPlayer.duration > 0) {
                binding.songSeekbar.max = exoPlayer.duration.toInt()
                binding.songSeekbar.progress = exoPlayer.currentPosition.toInt()
            }
            binding.songRunningTimeStamp.text = convertMillisToTime(exoPlayer.currentPosition)
            handler.postDelayed(playbackProgressRunnable, 500)
        }

        binding.layout.addTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                if (binding.root.progress != 1f) {
                    vm.animationOnProgress.postValue(binding.root.progress)
                }
            }
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {}
            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
        })
    }

    private fun observer() {
        vm.currentSong.observe(requireActivity()) {
            binding.itemSongName.text = it.song.name
            binding.itemSongSinger.text = it.song.artist
            Glide.with(requireContext())
                .asBitmap()
                .load(it.song.getImageUrl())
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 12)))
                .into(binding.background)
            playSong(it.song)
        }

        vm.stateOpened.observe(requireActivity()) {
            isUpdating = true
            if(it) {
                binding.itemSongName.textSize = 22f
                binding.itemSongSinger.visibility = View.VISIBLE
                binding.layout.transitionToStart()
            } else {
                binding.itemSongSinger.visibility = View.GONE
                binding.itemSongName.textSize = 18f
                binding.layout.transitionToEnd()
            }
        }

    }

    private fun listeners() {
        binding.buttonPlay.setOnClickListener {
            if(isPlaying) pauseSong() else playSong()
        }

        binding.songSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    exoPlayer.seekTo(progress.toLong())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.buttonRewind.setOnClickListener {
            exoPlayer.seekTo(exoPlayer.currentPosition - 10000)
        }

        binding.buttonForward.setOnClickListener {
            exoPlayer.seekTo(exoPlayer.currentPosition + 10000)
        }
    }

    fun playSong(song :Song? = null) {
        isPlaying = true
        Glide.with(requireContext()).load(R.drawable.ic_pause).into(binding.buttonPlay)
        if(song == null) {
            exoPlayer.play()
            return
        }
        val mediaItem = MediaItem.fromUri(song.url ?: "")
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        "0:00".let {
            binding.songEndTimeStamp.text = it
            binding.songRunningTimeStamp.text = it
        }

    }

    fun pauseSong() {
        isPlaying = false
        exoPlayer.pause()
        Glide.with(requireContext()).load(R.drawable.ic_play).into(binding.buttonPlay)
    }

    fun convertMillisToTime(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    override fun onStart() {
        super.onStart()
        handler.post(playbackProgressRunnable)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(playbackProgressRunnable)
    }
}