package com.delighted2wins.climify.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng

class LocationHelper(private val context: Context) {
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    init {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @SuppressLint("MissingPermission")
    fun getLocationCoordinates(callback: (LatLng?) -> Unit) {
        locationListener = LocationListener { location ->
            if (location.latitude != 0.0 && location.longitude != 0.0) {
                callback(LatLng(location.latitude, location.longitude))
                removeLocationUpdates()
            }
        }

        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0L,
            0f,
            locationListener!!
        )
    }

    private fun removeLocationUpdates() {
        locationListener?.let {
            locationManager?.removeUpdates(it)
            locationListener = null
        }
    }
}