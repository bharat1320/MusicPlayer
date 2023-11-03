package com.androidji.musicplayer.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidji.musicplayer.data.CurrentSong
import com.androidji.musicplayer.data.Song
import com.androidji.musicplayer.data.SongPlayerState
import com.androidji.musicplayer.data.SongsResponse
import com.androidji.musicplayer.ui.viewModels.repository.SongsRepository

class MainViewModel: ViewModel() {
    var repository = SongsRepository(this)

    var songsList : MutableLiveData<SongsResponse> = MutableLiveData()

    var currentSongPlaylist : MutableLiveData<ArrayList<Song>> = MutableLiveData()

    var currentSong : MutableLiveData<CurrentSong> = MutableLiveData()

    var currentSongPlayerState : MutableLiveData<SongPlayerState> = MutableLiveData()

    var currentSongPlayingState : MutableLiveData<Boolean> = MutableLiveData()

}