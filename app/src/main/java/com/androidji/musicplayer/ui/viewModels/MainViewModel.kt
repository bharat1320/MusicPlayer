package com.androidji.musicplayer.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidji.musicplayer.data.SongsResponse
import com.androidji.musicplayer.ui.viewModels.repository.SongsRepository

class MainViewModel: ViewModel() {
    var repository = SongsRepository(this)

    var songsList : MutableLiveData<SongsResponse> = MutableLiveData()

    var currentSongId : MutableLiveData<Int> = MutableLiveData()

    var stateOpened : MutableLiveData<Boolean> = MutableLiveData()
}