package com.BusMap.busmapsv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.BusMap.busmapsv.Mapas
import com.BusMap.busmapsv.R

class MapsAdapter(private var mapsList: List<Mapas>, private val onClickListener: (Mapas) -> Unit) :
    RecyclerView.Adapter<MapsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MapsViewHolder(layoutInflater.inflate(R.layout.items_maps, parent, false))
    }

    override fun getItemCount(): Int {
        return mapsList.size

    }

    override fun onBindViewHolder(holder: MapsViewHolder, position: Int) {
        val item = mapsList[position]
        holder.render(item, onClickListener)
    }

    fun updataMaps(mapsList: List<Mapas>) {
        this.mapsList = mapsList
        notifyDataSetChanged()

    }
}