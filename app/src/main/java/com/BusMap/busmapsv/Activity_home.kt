package com.BusMap.busmapsv

// Importar las dependencias necesarias
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.BusMap.busmapsv.adapter.MapsAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class Activity_home : AppCompatActivity() {

    private var mapsMutableList: MutableList<Mapas> = MapsProvider.mapsList.toMutableList()
    private var adapter: MapsAdapter? = null
    private var count = 0
    private var interstitial: InterstitialAd? = null
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val filterMaps = findViewById<EditText>(R.id.txtBuscar)
        filterMaps.addTextChangedListener { filter ->
            val MapsFiltered = mapsMutableList.filter { maps ->
                maps.keywords.lowercase().contains(filter.toString().lowercase())
            }
            adapter?.updataMaps(MapsFiltered)
        }
        initRecyclerView()
        //Guardar datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        initLoadAds()
        initAds()
    }

    private fun initLoadAds() {
        val adRequest = AdRequest.Builder().build()
        val adView = findViewById<AdView>(R.id.bannerAd)
        adView.loadAd(adRequest)
    }

    private fun initAds() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, object :
            InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                interstitial = interstitialAd
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                interstitial = null
            }
        })
    }

    fun initRecyclerView() {
        adapter = MapsAdapter(mapsMutableList) { mapas ->
            onItenClick(mapas)
        }
        val manager = LinearLayoutManager(this)
        val decoration = DividerItemDecoration(this, manager.orientation)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMaps)
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpacesItemDecoration(25))
    }

    fun onItenClick(mapas: Mapas) {
        // En lugar de mostrar un Toast, iniciamos la nueva Activity
        val intent = Intent(this, RouteDetailsActivity::class.java)

        // Pasamos los datos del objeto 'mapas' a la nueva Activity
        intent.putExtra("name", mapas.name)
        intent.putExtra("description", mapas.description)
        intent.putExtra("fee", mapas.fee)
        intent.putExtra("timeTravel", mapas.timeTravel)
        intent.putExtra("url", mapas.url)
        intent.putExtra("start", mapas.start)
        intent.putExtra("end", mapas.end)

        count += 1
        checkCounter()
        // Iniciar la nueva Activity
        startActivity(intent)
    }

    private fun checkCounter() {
        if (count == 1) {
            showAds()
            count = 0
            initAds()
        }
    }

    fun showAds() {
        interstitial?.show(this)
    }

    // Clase que crea un espaciado entre los elementos del RecyclerView
    class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: android.view.View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.bottom = space
        }
    }
}
