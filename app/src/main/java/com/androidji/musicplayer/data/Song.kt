package com.androidji.musicplayer.data

import com.androidji.musicplayer.utils.URLS
import com.google.gson.annotations.SerializedName

data class SongsResponse(
    @SerializedName("data" ) var data : ArrayList<Song> = arrayListOf()
)
data class Song(
    @SerializedName("id"           ) var id          : Int?     = null,
    @SerializedName("status"       ) var status      : String?  = null,
    @SerializedName("sort"         ) var sort        : String?  = null,
    @SerializedName("user_created" ) var userCreated : String?  = null,
    @SerializedName("date_created" ) var dateCreated : String?  = null,
    @SerializedName("user_updated" ) var userUpdated : String?  = null,
    @SerializedName("date_updated" ) var dateUpdated : String?  = null,
    @SerializedName("name"         ) var name        : String?  = null,
    @SerializedName("artist"       ) var artist      : String?  = null,
    @SerializedName("accent"       ) var accent      : String?  = null,
    @SerializedName("cover"        ) var cover       : String?  = null,
    @SerializedName("top_track"    ) var topTrack    : Boolean? = null,
    @SerializedName("url"          ) var url         : String?  = null
) {
    fun getImageUrl() = URLS.BASE_URL + URLS.getSongCover + cover
}

data class CurrentSong(
    var song : Song,
    var position :Int
)