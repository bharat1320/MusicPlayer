package com.androidji.musicplayer.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidji.musicplayer.data.CurrentSong
import com.androidji.musicplayer.data.SongsResponse
import com.androidji.musicplayer.ui.viewModels.repository.SongsRepository

class MainViewModel: ViewModel() {
    var repository = SongsRepository(this)

    var songsList : MutableLiveData<SongsResponse> = MutableLiveData()

    var currentSong : MutableLiveData<CurrentSong> = MutableLiveData()

    var stateOpened : MutableLiveData<Boolean> = MutableLiveData()

    var animationOnProgress : MutableLiveData<Float> = MutableLiveData()

    var animationCompleted : MutableLiveData<Boolean> = MutableLiveData()

}