package com.example.googlmapdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.googlmapdemo.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity() {

    private var rootView: ActivityMainBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootView = ActivityMainBinding.inflate(layoutInflater);
        setContentView(rootView!!.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fuelStation()

        rootView!!.mapView.onCreate(savedInstanceState)

    }
    override fun onResume() {
        super.onResume()
        rootView!!.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        rootView!!.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        rootView!!.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        rootView!!.mapView.onLowMemory()
    }
    private fun fuelStation() {

        val locationArrayList = ArrayList<Location>()

        locationArrayList.add(Location(24.9167, 67.0833,"Gulshan Iqbal")) // Gulshan Iqbal
        locationArrayList.add(Location(24.825671, 67.13184,"Korangi Crek")) // Korangi Crek
        locationArrayList.add(Location(24.773193, 67.076247,"DHA KARACHI")) // DHA Karachi
          // New York City

        //locationArrayList.add("","")

        rootView!!.mapView.getMapAsync { googleMap ->
            // Save a reference to the GoogleMap
            //this.mMap = googleMap

            mMap = googleMap;



            rootView!!.btnZoomIn.setOnClickListener {
                mMap?.animateCamera(CameraUpdateFactory.zoomIn())
            }

            rootView!!.btnZoomOut.setOnClickListener {
                mMap?.animateCamera(CameraUpdateFactory.zoomOut())
            }

            rootView!!.myLocationCustomButton.setOnClickListener(){
                Log.e("@@@","Pressed")
                checkLocationPermission()
            }

            for (location in locationArrayList) {
                val latLng = LatLng(location.latitude, location.longitude)
                val markerOptions = MarkerOptions().position(latLng).title(location.title)
                mMap?.addMarker(markerOptions)
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11f))
            }
//            for (i in locationArrayList!!.indices) {
//                var location = LatLng(locationArrayList!![i].latitude!!.toDouble(), locationArrayList!![i].longitude!!.toDouble())
//                mMap?.addMarker(MarkerOptions().position(location).title(locationArrayList!![i].title))
//                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 11f))
//            }
        }

    }
    private fun checkLocationPermission() {
        //Log.e("###","CheckLocation")
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("###","CheckLocation")
            fetchLocation()
            // Permission already granted, fetch location

        } else {
            Log.e("###","else")
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    Toast.makeText(this, currentLatLng.toString(), Toast.LENGTH_SHORT).show()

                    mMap?.clear()
                    mMap?.addMarker(MarkerOptions().position(currentLatLng).title("My Location"))
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                } else {
                    Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Handle location retrieval failure here (e.g., show an error message)
                Toast.makeText(this, "Location retrieval failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//            if (location != null) {
//                val currentLatLng = LatLng(location.latitude, location.longitude)
//                Toast.makeText(this, currentLatLng.toString(), Toast.LENGTH_SHORT).show()
//
//                mMap!!.clear()
//                mMap!!.addMarker(MarkerOptions().position(currentLatLng).title("My Location"))
//                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
//            } else {
//                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
