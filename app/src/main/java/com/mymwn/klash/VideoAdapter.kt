package com.mymwn.klash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mymwn.klash.databinding.ItemVideoBinding

class VideoAdapter(private val items: List<VideoItem>) :
    RecyclerView.Adapter<VideoAdapter.Holder>() {

    class Holder(val b: ItemVideoBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.b.name.text = item.name
        holder.b.path.text = item.folder
    }

    override fun getItemCount() = items.size
}
