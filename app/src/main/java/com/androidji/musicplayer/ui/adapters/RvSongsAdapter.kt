package com.androidji.musicplayer.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.androidji.musicplayer.data.CurrentSong
import com.androidji.musicplayer.data.Song
import com.androidji.musicplayer.databinding.RvSongItemBinding
import com.androidji.musicplayer.utils.URLS.Companion.BASE_URL
import com.androidji.musicplayer.utils.URLS.Companion.getSongCover
import com.bumptech.glide.Glide

class RvSongsAdapter(var context: Context,
                     var songs : ArrayList<Song>,
                     var listener : (currentSong : CurrentSong) -> Unit = {}
) : RecyclerView.Adapter<RvSongsAdapter.RvSongsViewHolder>() {

    inner class RvSongsViewHolder(var binding : RvSongItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvSongsViewHolder {
        var layout = RvSongItemBinding.inflate(LayoutInflater.from(context))
        layout.root.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        return RvSongsViewHolder(layout)
    }

    override fun getItemCount() = songs.size

    fun refreshData(newData :ArrayList<Song>) {
        songs.clear()
        songs.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RvSongsViewHolder, position: Int) {
        val binding = holder.binding

        songs[position].let {
            binding.itemSongName.text = it.name ?: ""
            binding.itemSongSinger.text = it.artist ?: ""
            Glide.with(context).load(it.getImageUrl()).circleCrop().into(binding.itemCoverImage)
            holder.itemView.setOnClickListener { view ->
                listener(CurrentSong(it,it.id?:0))
            }
        }
    }
}