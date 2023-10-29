package com.androidji.musicplayer.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.androidji.musicplayer.data.Song
import com.androidji.musicplayer.databinding.RvSongPlayerItemBinding
import com.androidji.musicplayer.utils.URLS
import com.bumptech.glide.Glide

class RvSongPlayerAdapter(var context: Context,
                          var songs : ArrayList<Song>
) : RecyclerView.Adapter<RvSongPlayerAdapter.RvSongPlayerViewHolder>() {

    inner class RvSongPlayerViewHolder(var binding : RvSongPlayerItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvSongPlayerViewHolder {
        var layout = RvSongPlayerItemBinding.inflate(LayoutInflater.from(context))
        layout.root.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return RvSongPlayerViewHolder(layout)
    }

    override fun getItemCount() = songs.size

    fun refreshData(newData :ArrayList<Song>) {
        songs.clear()
        songs.addAll(newData)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RvSongPlayerViewHolder, position: Int) {
        val binding = holder.binding

        songs[position].let {
            it.cover?.let { cover ->
                Glide.with(context).load(URLS.BASE_URL + URLS.getSongCover + cover).into(binding.itemSongCover)
            }
        }
    }
}