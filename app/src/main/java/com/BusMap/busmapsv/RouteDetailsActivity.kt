package com.BusMap.busmapsv

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RouteDetailsActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_details)

        // Obtener los datos del Intent
        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("description")
        val fee = intent.getStringExtra("fee")
        val timeTravel = intent.getStringExtra("timeTravel")
        val url = intent.getStringExtra("url")
        val inicio = intent.getStringExtra("start")
        val fin = intent.getStringExtra("end")
        Log.d("IntentValues", "start: $inicio, end: $fin")

        // Asignar los datos a las vistas
        val nameTextView = findViewById<TextView>(R.id.routeName)
        val descriptionTextView = findViewById<TextView>(R.id.routeDescription)
        val tarifaTextView = findViewById<TextView>(R.id.routeTarifa)
        val timeTravelTextView = findViewById<TextView>(R.id.routeTime)
        val urlView = findViewById<WebView>(R.id.urlMap)
        val startTextView = findViewById<TextView>(R.id.textStart)
        val endTextView = findViewById<TextView>(R.id.textEnd)

        nameTextView.text = name
        descriptionTextView.text = description
        tarifaTextView.text = fee
        timeTravelTextView.text = timeTravel
        startTextView.text = inicio
        endTextView.text = fin
        val settings = urlView.settings

        settings.javaScriptEnabled = true
        if (url != null) {
            urlView.webViewClient = WebViewClient()
            urlView.loadUrl(url)
        }

    }
}
