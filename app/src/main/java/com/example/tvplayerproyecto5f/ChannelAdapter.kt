package com.example.tvplayerproyecto5f

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ChannelAdapter(
    private val channels: List<Channel>,
    private val onChannelClick: (Channel) -> Unit,
    private val onChannelLongClick: (Channel, View) -> Unit
) : RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.channel_list_item, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channel = channels[position]
        holder.bind(channel)
        holder.itemView.setOnClickListener { onChannelClick(channel) }
        holder.itemView.setOnLongClickListener { 
            onChannelLongClick(channel, holder.itemView)
            true
        }
    }

    override fun getItemCount(): Int = channels.size

    class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val channelName: TextView = itemView.findViewById(R.id.channel_name)
        private val channelLogo: ImageView = itemView.findViewById(R.id.channel_logo)

        fun bind(channel: Channel) {
            channelName.text = channel.name
            Glide.with(itemView.context)
                .load(channel.logo)
                .placeholder(R.mipmap.ic_launcher)
                .into(channelLogo)
        }
    }
}