package com.androidji.musicplayer.ui.viewModels.repository

import com.androidji.musicplayer.api.SongsApi
import com.androidji.musicplayer.network.RetrofitInstance
import com.androidji.musicplayer.ui.viewModels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class SongsRepository(var vm : MainViewModel) {
    private val api = RetrofitInstance.createLoginService().create(SongsApi::class.java)

    fun getSongList() {
        CoroutineScope(Dispatchers.IO).launch {
            try{
                vm.songsList.postValue(api.getSongsList())
            } catch (e : Exception) {
                // Error Handling
            }
        }
    }

}