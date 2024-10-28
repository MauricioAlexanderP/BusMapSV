package com.BusMap.busmapsv

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions

class RouteDetailsActivityPremium : AppCompatActivity(), OnMapReadyCallback {
    //codigoa agregado
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isPremium = true

    @SuppressLint("SetJavaScriptEnabled", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_details_premium)

        // Inicializar FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        var routeView = findViewById<MapView>(R.id.routeView)
        // Inicializar MapView
        mapView = findViewById(R.id.routeView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this) // Asignar el callback para cuando el mapa esté listo


        // Obtener los datos del Intent
        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("description")
        val fee = intent.getStringExtra("fee")
        val timeTravel = intent.getStringExtra("timeTravel")
        val inicio = intent.getStringExtra("start")
        val fin = intent.getStringExtra("end")
        // Recibir la lista de rutas
        val route = intent.getParcelableArrayListExtra<LatLng>("route")


        // Asignar los datos a las vistas
        val nameTextView = findViewById<TextView>(R.id.routeName)
        val descriptionTextView = findViewById<TextView>(R.id.routeDescription)
        val tarifaTextView = findViewById<TextView>(R.id.routeTarifa)
        val timeTravelTextView = findViewById<TextView>(R.id.routeTime)
        //val urlView = findViewById<WebView>(R.id.urlMap)
        val startTextView = findViewById<TextView>(R.id.textStart)
        val endTextView = findViewById<TextView>(R.id.textEnd)

        nameTextView.text = name
        descriptionTextView.text = description
        tarifaTextView.text = fee
        timeTravelTextView.text = timeTravel
        startTextView.text = inicio
        endTextView.text = fin
        Log.d("IntentValues", "inicio: $inicio")
        Log.d("IntentValues", "fin: $fin")

        initLoadAds()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        enableLocationTracking()

        // Obtener la ruta desde el Intent
        val route = intent.getParcelableArrayListExtra<LatLng>("route")
        Log.d("RouteDetailsActivity", "Ruta recibida: $route")
        // Dibujar la ruta en el mapa
        route?.let {
            val polylineOptions =
                PolylineOptions().addAll(it).color(android.graphics.Color.BLUE).width(5f)
            googleMap.addPolyline(polylineOptions)

            // Mover la cámara al primer punto de la ruta
            if (it.isNotEmpty()) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it[0], 15f))
            }
        }
    }

    // Habilitar la actualización de la ubicación en tiempo real
    private fun enableLocationTracking() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            return
        }

        googleMap.isMyLocationEnabled = true

        // Configurar la solicitud de actualizaciones de ubicación
        val locationRequest = LocationRequest.create().apply {
            interval = 1000 // cada 10 segundos
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.let {
                    val location = it.lastLocation
                    if (location != null) {
                        updateLocationOnMap(location)
                    }
                }
            }
        }, Looper.getMainLooper())
    }

    // Actualizar la ubicación del usuario en el mapa
    private fun updateLocationOnMap(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        // Mover la cámara para que siga la ubicación del usuario
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
    }

    // Solicitar permisos en tiempo de ejecución
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableLocationTracking()
        } else {
            Toast.makeText(this, "Permisos de ubicación requeridos", Toast.LENGTH_SHORT).show()
        }
    }


    // Métodos para el ciclo de vida de MapView
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    //anuncios
    private fun initLoadAds() {
        val adRequest = AdRequest.Builder().build()
        val adView = findViewById<AdView>(R.id.bannerAd)
        adView.loadAd(adRequest)
    }
}