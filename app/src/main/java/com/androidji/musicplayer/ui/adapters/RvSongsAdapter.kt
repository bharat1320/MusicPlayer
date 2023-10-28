package com.androidji.musicplayer.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androidji.musicplayer.data.Song
import com.androidji.musicplayer.databinding.RvSongItemBinding
import com.androidji.musicplayer.utils.URLS.Companion.BASE_URL
import com.androidji.musicplayer.utils.URLS.Companion.getSongCover
import com.bumptech.glide.Glide

class RvSongsAdapter(var context: Context,
                     var songs : ArrayList<Song>,
                     var listener : (id :Int) -> Unit = {}
) : RecyclerView.Adapter<RvSongsAdapter.RvSongsViewHolder>() {

    inner class RvSongsViewHolder(var binding : RvSongItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvSongsViewHolder {
        return RvSongsViewHolder(RvSongItemBinding.inflate(LayoutInflater.from(context)))
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
            it.cover?.let { cover ->
                Glide.with(context).load(BASE_URL + getSongCover + cover).circleCrop().into(binding.itemCoverImage)
            }
            holder.itemView.setOnClickListener { view ->
                listener(it.id?:0)
            }
        }
    }
}