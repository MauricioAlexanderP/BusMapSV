package com.BusMap.busmapsv.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.BusMap.busmapsv.Mapas
import com.BusMap.busmapsv.MapsProvider
import com.BusMap.busmapsv.R
import com.BusMap.busmapsv.RouteDetailsActivity
import com.BusMap.busmapsv.adapter.MapsAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class HomeFragment : Fragment() {

    private var mapsMutableList: MutableList<Mapas> = MapsProvider.mapsList.toMutableList()
    private var adapter: MapsAdapter? = null
    private var count = 0
    private var interstitial: InterstitialAd? = null

    @SuppressLint("SetJavaScriptEnabled", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        enableEdgeToEdge(view)

        // Configuración de los insets del sistema
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val filterMaps = view.findViewById<EditText>(R.id.txtBuscar)
        filterMaps.addTextChangedListener { filter ->
            val MapsFiltered = mapsMutableList.filter { maps ->
                maps.keywords.lowercase().contains(filter.toString().lowercase())
            }
            adapter?.updataMaps(MapsFiltered)
        }

        // Inicializar RecyclerView
        initRecyclerView(view)

        // Cargar anuncios
        initLoadAds(view)
        initAds()

        return view
    }

    override fun onResume() {
        super.onResume()

        // Cambiar el título del toolbar
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Inicio"
    }

    private fun initLoadAds(view: View) {
        val adRequest = AdRequest.Builder().build()
        val adView = view.findViewById<AdView>(R.id.bannerAd)
        adView.loadAd(adRequest)
    }

    private fun initAds() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireContext(),
            "ca-app-pub-1716910392295602/2154674097",
            adRequest,
            object :
                InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    interstitial = interstitialAd
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    interstitial = null
                }
            })
    }

    private fun initRecyclerView(view: View) {
        adapter = MapsAdapter(mapsMutableList) { mapas ->
            onItenClick(mapas)
        }
        val manager = LinearLayoutManager(requireContext())
        val decoration = DividerItemDecoration(requireContext(), manager.orientation)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewMaps)
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpacesItemDecoration(25))
    }

    private fun onItenClick(mapas: Mapas) {
        // Navegar a otra activity o fragmento con los datos del objeto 'mapas'
        val intent = Intent(requireContext(), RouteDetailsActivity::class.java)

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

    private fun showAds() {
        interstitial?.show(requireActivity())
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

    private fun enableEdgeToEdge(view: View) {
        // Implementación de cualquier lógica relacionada con Edge to Edge, si es necesario
    }
}