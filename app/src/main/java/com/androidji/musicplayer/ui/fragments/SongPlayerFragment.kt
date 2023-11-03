package com.androidji.musicplayer.ui.fragments

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.graphics.ColorUtils.HSLToColor
import androidx.core.graphics.ColorUtils.calculateLuminance
import androidx.core.graphics.ColorUtils.colorToHSL
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import com.androidji.musicplayer.R
import com.androidji.musicplayer.data.CacheImage
import com.androidji.musicplayer.data.CurrentSong
import com.androidji.musicplayer.data.Song
import com.androidji.musicplayer.data.SongPlayerState
import com.androidji.musicplayer.databinding.FragmentSongPlayerBinding
import com.androidji.musicplayer.ui.viewModels.MainViewModel
import com.androidji.musicplayer.utils.HelperUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.material.color.utilities.ColorUtils
import java.util.concurrent.TimeUnit

class SongPlayerFragment : Fragment() {
    lateinit var binding: FragmentSongPlayerBinding
    lateinit var vm : MainViewModel

    var isPlaying = false
    var cacheImage : CacheImage? = null
    var playbackTimeMillis = 0L
    private lateinit var playerNotificationManager: PlayerNotificationManager
    lateinit var songsViewPagerFragment : SongsViewPagerFragment

    val CHANNEL_ID = "your_channel_id"
    val NOTIFICATION_ID = 1

    private lateinit var exoPlayer: ExoPlayer
    lateinit var audioManager: AudioManager
    lateinit var playbackProgressRunnable : Runnable
    private val handler = Handler(Looper.getMainLooper())
    private val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                pauseSong()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                pauseSong()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                pauseSong()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                playSong()
            }
        }
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
        songsViewPagerFragment = SongsViewPagerFragment()

        HelperUtils.replaceFragment(requireActivity(),binding.fragmentSongs.id, songsViewPagerFragment)

        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager

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
                                playbackTimeMillis = exoPlayer.duration
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
    }

    private fun observer() {
        vm.currentSong.observe(requireActivity()) {
            binding.itemSongName.text = it.song.name
            binding.itemSongSinger.text = it.song.artist
            HelperUtils.doInBackground {
                Glide.with(requireContext())
                    .asBitmap()
                    .load(it.song.getImageUrl())
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val palette = Palette.from(resource).generate()
                            val dominantColor = palette.getDominantColor(Color.BLACK)
                            val vibrantColor = palette.getVibrantColor(Color.BLACK)
                            val gradientDrawable = GradientDrawable(
                                GradientDrawable.Orientation.TOP_BOTTOM,
                                intArrayOf(vibrantColor, dominantColor)
                            )
                            HelperUtils.doInMain {
                                binding.background.background = gradientDrawable
                            }
                            vm.currentSongPlayerState.postValue(SongPlayerState(
                                it.song.name ?:"",
                                it.song.getImageUrl(),
                                gradientDrawable,
                            ))
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }
            playSong(it.song)
            createPlayerNotificationManager(requireActivity(),exoPlayer,it)
        }

        vm.currentSongPlayingState.observe(requireActivity()) {
            if(it) pauseSong() else playSong()
        }
    }

    private fun listeners() {
        binding.buttonPlay.setOnClickListener {
            HelperUtils.giveHapticFeedback(requireActivity())
            vm.currentSongPlayingState.postValue(!(vm.currentSongPlayingState.value ?: false))
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

    fun createPlayerNotificationManager(
        context: Context,
        exoPlayer: ExoPlayer,
        it: CurrentSong
    ) {
        val mediaSession = MediaSessionCompat(context, "TAG")

        playerNotificationManager =
            PlayerNotificationManager.Builder(context, NOTIFICATION_ID, CHANNEL_ID)
                .setChannelNameResourceId(R.string.notification_channel_name)
                .setChannelDescriptionResourceId(R.string.notification_channel_description)
                .setMediaDescriptionAdapter(object :
                    PlayerNotificationManager.MediaDescriptionAdapter {
                    override fun getCurrentContentTitle(player: Player): String {
                        return it.song.name ?: ""
                    }

                    override fun createCurrentContentIntent(player: Player): PendingIntent? {
                        return null
                    }

                    override fun getCurrentContentText(player: Player): String? {
                        return it.song.artist ?: ""
                    }

                    override fun getCurrentLargeIcon(
                        player: Player,
                        callback: PlayerNotificationManager.BitmapCallback
                    ): Bitmap? {
                        if(cacheImage == null || cacheImage?.imageId == it.song.id) {
                            var bitmap: Bitmap? = null
                            Glide.with(requireContext())
                                .asBitmap()
                                .load(it.song.getImageUrl())
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {
                                        cacheImage = CacheImage(it.song.id ?: 0,resource)
                                        bitmap = makeBitmapSquare(resource)
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {}
                                })
                            return bitmap
                        } else {
                            return cacheImage!!.image
                        }
                    }
                })
                .setNotificationListener(object :
                    PlayerNotificationManager.NotificationListener {
                    override fun onNotificationCancelled(
                        notificationId: Int,
                        dismissedByUser: Boolean
                    ) {
                        pauseSong()
                    }

                    override fun onNotificationPosted(
                        notificationId: Int,
                        notification: Notification,
                        ongoing: Boolean
                    ) {
                        // Manage the notification posting, like starting or stopping the foreground service.
                    }
                })
                .build()

        playerNotificationManager.setPlayer(exoPlayer)
        playerNotificationManager.setUseFastForwardAction(true)
        playerNotificationManager.setUsePlayPauseActions(true)
        playerNotificationManager.setUseNextAction(false)
        playerNotificationManager.setUsePreviousAction(false)
        playerNotificationManager.setMediaSessionToken(mediaSession.sessionToken)
    }

    fun makeBitmapSquare(bitmap: Bitmap): Bitmap {
        val size = Math.min(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2
        return Bitmap.createBitmap(bitmap, x, y, size, size)
    }

    fun playSong(song :Song? = null) {
        val result = audioManager.requestAudioFocus(
            afChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            isPlaying = true
            Glide.with(requireContext()).load(R.drawable.play_to_pause).into(binding.buttonPlay)
            if (song == null) {
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
    }

    fun pauseSong() {
        isPlaying = false
        exoPlayer.pause()
        Glide.with(requireContext()).load(R.drawable.pause_to_play).into(binding.buttonPlay)
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
        handler.removeCallbacks(playbackProgressRunnable)
        super.onStop()
    }

    override fun onDestroy() {
        audioManager.abandonAudioFocus(afChangeListener)
        super.onDestroy()
    }
}