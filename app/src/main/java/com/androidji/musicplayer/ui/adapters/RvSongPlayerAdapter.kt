package com.androidji.musicplayer.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.androidji.musicplayer.data.Song
import com.androidji.musicplayer.databinding.RvSongPlayerItemBinding
import com.androidji.musicplayer.utils.URLS
import com.androidji.musicplayer.utils.utils.Companion.dpToPx
import com.bumptech.glide.Glide

class RvSongPlayerAdapter(var context: Context,
                          var songs : ArrayList<Song>,
                          var cardWidth : Int
) : RecyclerView.Adapter<RvSongPlayerAdapter.RvSongPlayerViewHolder>() {

    inner class RvSongPlayerViewHolder(var binding : RvSongPlayerItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvSongPlayerViewHolder {
        var layout = RvSongPlayerItemBinding.inflate(LayoutInflater.from(context))
        layout.root.layoutParams = FrameLayout.LayoutParams(
            cardWidth,
            (cardWidth * 1.4).toInt()
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

        if(position % 2 == 0) {
            binding.itemSongCover.setPadding(0,0,0, dpToPx(4F).toInt())
        } else {
            binding.itemSongCover.setPadding(0, dpToPx(4F).toInt(),0,0)
        }

        songs[position].let {
            Glide.with(context).load(it.getImageUrl()).into(binding.itemSongCover)
        }
    }
}