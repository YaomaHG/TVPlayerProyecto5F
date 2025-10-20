package com.example.tvplayerproyecto5f

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// El adaptador para el RecyclerView que muestra la lista de canales.
class ChannelAdapter(
    private val channels: List<Channel>, // La lista de canales a mostrar.
    private val onChannelClick: (Channel) -> Unit, // Lambda que se ejecuta al hacer clic en un canal.
    private val onChannelLongClick: (Channel, View) -> Unit // Lambda que se ejecuta con un clic largo.
) : RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {

    // Crea una nueva vista para un ítem de la lista cuando es necesario.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.channel_list_item, parent, false)
        return ChannelViewHolder(view)
    }

    // Vincula los datos de un canal a una vista de ítem específica.
    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channel = channels[position]
        holder.bind(channel) // Llama a la función que asigna los datos a la vista.
        // Configura los listeners para clics.
        holder.itemView.setOnClickListener { onChannelClick(channel) }
        holder.itemView.setOnLongClickListener { 
            onChannelLongClick(channel, holder.itemView)
            true
        }
    }

    // Devuelve el número total de ítems en la lista.
    override fun getItemCount(): Int = channels.size

    // Representa una vista de un ítem individual en la lista.
    class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val channelName: TextView = itemView.findViewById(R.id.channel_name)
        private val channelLogo: ImageView = itemView.findViewById(R.id.channel_logo)

        // Asigna los datos del canal a los componentes de la vista.
        fun bind(channel: Channel) {
            channelName.text = channel.name // Pone el nombre del canal.
            // Utiliza Glide para cargar la imagen del logo desde la URL.
            Glide.with(itemView.context)
                .load(channel.logo)
                .placeholder(R.mipmap.ic_launcher) // Muestra una imagen por defecto mientras carga.
                .into(channelLogo) // Carga la imagen en el ImageView.
        }
    }
}
