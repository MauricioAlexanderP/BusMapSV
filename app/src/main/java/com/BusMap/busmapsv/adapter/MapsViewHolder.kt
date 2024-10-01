package com.BusMap.busmapsv.adapter

import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.BusMap.busmapsv.Mapas
import com.BusMap.busmapsv.R


class MapsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val name = view.findViewById<TextView>(R.id.tvName)
    val description = view.findViewById<TextView>(R.id.tvDescription)
    val fee = view.findViewById<TextView>(R.id.tvFee)
    val timeTravel = view.findViewById<TextView>(R.id.tvTimeTravel)
    val url = view.findViewById<WebView>(R.id.ivMap)
    fun render(mapas: Mapas, onClickListener: (Mapas) -> Unit) {
        name.text = mapas.name
        description.text = mapas.description
        fee.text = mapas.fee
        timeTravel.text = mapas.timeTravel
        url.webViewClient = WebViewClient()
        url.loadUrl(mapas.url)

        itemView.setOnClickListener {
            onClickListener(mapas)
        }
    }
}