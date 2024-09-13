package com.BusMap.busmapsv

import android.content.Context
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Activity_home : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Guardar datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)

        // Obtener referencia al WebView y al botón
        val webView: WebView = findViewById(R.id.webView)
        val loadButton: Button = findViewById(R.id.btnCargarMapa)

        // Habilitar JavaScript si es necesario
        webView.settings.javaScriptEnabled = true

        // Evitar que se abra un navegador externo
        webView.webViewClient = WebViewClient()

        // Asignar el evento de clic al botón
        loadButton.setOnClickListener {
            // Cargar la URL en el WebView
            webView.loadUrl("https://api.mapbox.com/styles/v1/busmap/cm0zeyfjq00ep01nqfhkwh8go/draft.html?title=view&access_token=pk.eyJ1IjoiYnVzbWFwIiwiYSI6ImNtMHplc3o0eDA1a2QyaXE4cW94c3V6MW4ifQ.pSxWCGCbp0h3_41MUGNiYw&zoomwheel=true&fresh=true#8.58/13.725/-88.8391")
        }
    }
}