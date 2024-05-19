package com.example.gasobp_daivol.activities


import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.gasobp_daivol.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.gasobp_daivol.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Verificar y solicitar permisos de ubicación
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Habilitar la capa de ubicación
            mMap.isMyLocationEnabled = true

            // Obtener la ubicación actual
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val miUbicacion = LatLng(it.latitude, it.longitude)

                        // Agregar un marcador azul en la ubicación actual
                        mMap.addMarker(
                            MarkerOptions()
                                .position(miUbicacion)
                                .title("Mi Ubicación")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        )

                        // Mover la cámara para enfocarse en la ubicación actual
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 15f))
                    }
                }
        } else {
            // Solicitar permisos de ubicación si no están concedidos
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }

                    // Lista de ubicaciones de gasolineras
        val gasolineras = listOf(
            LatLng(39.12525901057742, -0.44950409012161696),
            LatLng(39.194137880225306, -0.4433253714020703),
            LatLng(39.110949135852174, -0.5237817854946295),
            LatLng(39.17554102009313, -0.2624035865234151)
        )

        // Añadir marcadores para cada ubicación
        for (gasolinera in gasolineras) {
            mMap.addMarker(MarkerOptions().position(gasolinera).title("Gasolinera"))
        }

    }
}
