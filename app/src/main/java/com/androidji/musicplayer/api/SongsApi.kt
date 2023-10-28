package com.androidji.musicplayer.api

import com.androidji.musicplayer.data.SongsResponse
import com.androidji.musicplayer.utils.URLS
import retrofit2.http.GET
import retrofit2.http.Query

interface SongsApi {
    @GET(URLS.getSongsList)
    suspend fun getSongsList() : SongsResponse
}