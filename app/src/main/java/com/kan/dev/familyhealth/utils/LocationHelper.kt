package com.kan.dev.familyhealth.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.helper.widget.MotionEffect
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.kan.dev.familyhealth.R
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date
import java.util.Locale


object LocationHelper {
    const val DEFAULT_ZOOM = 55f
    const val FRIEND_ALL = "all"
    const val FRIEND_ONE = "one"
    fun getCurrentLocation(context: Context?, callback: (Any) -> Unit) {
        try {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(MotionEffect.TAG, "getCurrentLocation: Location permission not granted")
                return
            }
            val location = LocationServices.getFusedLocationProviderClient(
                context
            ).lastLocation
            location.addOnCompleteListener { task: Task<Location?> ->
                if (task.isSuccessful && task.result != null) {
                    val currentLocation = task.result
                    val latlng =
                        LatLng(currentLocation!!.latitude, currentLocation.longitude)
                    callback(latlng)
                } else {
                    Log.e(
                        MotionEffect.TAG,
                        "getCurrentLocation: Failed to get location"
                    )
                    callback(LatLng(-1.0, -1.0))
                }
            }
        } catch (e: Exception) {
            Log.e(MotionEffect.TAG, "getCurrentLocation: " + e.message)
        }
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var isGpsEnabled = false
        var isNetworkEnabled = false
        try {
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isGpsEnabled || isNetworkEnabled
    }

    fun findLargestLatLngBounds(latLngList: List<LatLng>): LatLngBounds {
        var left = latLngList[0].longitude
        var right = latLngList[0].longitude
        var top = latLngList[0].latitude
        var bottom = latLngList[0].latitude
        for (latLng in latLngList) {
            val lng = latLng.longitude
            val lat = latLng.latitude
            if (lng > right) {
                right = lng
            }
            if (lng < left) {
                left = lng
            }
            if (lat > top) {
                top = lat
            }
            if (lat < bottom) {
                bottom = lat
            }
        }
        val builder = LatLngBounds.Builder()
        builder.include(LatLng(top, left))
        builder.include(LatLng(bottom, right))
        return builder.build()
    }


    fun getAddressFromLatLng(context: Context?, latLng: LatLng): String {
        val geocoder = Geocoder(context!!)
        var address = ""
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        } catch (e: IOException) {
            Log.e(MotionEffect.TAG, "onLocationReceived: " + e.message)
        }
        if (addresses != null) {
            address = addresses[0].getAddressLine(0)
        }
        return address
    }

    fun getNearFriend(context: Context) {
        Places.initialize(context, context.getString(R.string.maps_api_key))
        val placesClient = Places.createClient(context)
        val request =
            FindCurrentPlaceRequest.builder(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG))
                .build()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        placesClient.findCurrentPlace(request)
            .addOnSuccessListener { response: FindCurrentPlaceResponse ->
                val placeLikelihoods =
                    response.placeLikelihoods
                if (!placeLikelihoods.isEmpty()) {
                    val place = placeLikelihoods[0].place
                    val placeName = place.name
                    val placeLatLng = place.latLng
                    Log.d(
                        "Nearby Location",
                        "Name: $placeName, LatLng: $placeLatLng"
                    )
                } else {
                    Log.d("Nearby Location", "No nearby locations found.")
                }
            }
    }

    fun convertToLatLng(latLngString: String): LatLng {
        var startIndex = latLngString.indexOf("(") + 1
        var endIndex = latLngString.indexOf(",")
        val latitudeString = latLngString.substring(startIndex, endIndex).trim { it <= ' ' }
        startIndex = endIndex + 1
        endIndex = latLngString.indexOf(")")
        val longitudeString = latLngString.substring(startIndex, endIndex).trim { it <= ' ' }
        val latitude = latitudeString.toDouble()
        val longitude = longitudeString.toDouble()
        return LatLng(latitude, longitude)
    }

    fun iconMarker(context: Context?, resId: Int): BitmapDescriptor {
        val drawable = AppCompatResources.getDrawable(context!!, getIconMaker(resId))
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth / 5,
            drawable.intrinsicHeight / 5,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun iconMarkerSos(context: Context?, resId: Int): BitmapDescriptor {
        val drawable = AppCompatResources.getDrawable(context!!, getIconMakerSos(resId))
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth / 5,
            drawable.intrinsicHeight / 5,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    @SuppressLint("NonConstantResourceId")
    fun getIconMaker(resId: Int): Int {
        when (resId) {
            R.drawable.ic_avt_1 -> {
                return R.drawable.ic_maker_avt_1
            }

            R.drawable.ic_avt_2 -> {
                return R.drawable.ic_maker_avt_2
            }

            R.drawable.ic_avt_3 -> {
                return R.drawable.ic_maker_avt_3
            }

            R.drawable.ic_avt_4 -> {
                return R.drawable.ic_maker_avt_4
            }

            R.drawable.ic_avt_5 -> {
                return R.drawable.ic_maker_avt_5
            }

            R.drawable.ic_avt_6 -> {
                return R.drawable.ic_maker_avt_6
            }

            R.drawable.ic_avt_7 -> {
                return R.drawable.ic_maker_avt_7
            }

            R.drawable.ic_avt_8 -> {
                return R.drawable.ic_maker_avt_8
            }

            R.drawable.ic_avt_9 -> {
                return R.drawable.ic_maker_avt_9
            }

            R.drawable.ic_avt_10 -> {
                return R.drawable.ic_maker_avt_10
            }

            R.drawable.ic_avt_11 -> {
                return R.drawable.ic_maker_avt_11
            }

            R.drawable.ic_avt_12 -> {
                return R.drawable.ic_maker_avt_12
            }

            R.drawable.ic_avt_13 -> {
                return R.drawable.ic_maker_avt_13
            }

            R.drawable.ic_avt_14 -> {
                return R.drawable.ic_maker_avt_14
            }

            R.drawable.ic_avt_15 -> {
                return R.drawable.ic_maker_avt_15
            }

            R.drawable.ic_avt_16 -> {
                return R.drawable.ic_maker_avt_16
            }

            R.drawable.ic_avt_17 -> {
                return R.drawable.ic_maker_avt_18
            }

            R.drawable.ic_avt_18 -> {
                return R.drawable.ic_maker_avt_17
            }
        }
        return R.drawable.ic_maker_avt_1
    }

    @SuppressLint("NonConstantResourceId")
    fun getIconMakerSos(resId: Int): Int {
        when (resId) {
            R.drawable.ic_avt_1 -> {
                return R.drawable.ic_maker_sos_1
            }

            R.drawable.ic_avt_2 -> {
                return R.drawable.ic_maker_sos_2
            }

            R.drawable.ic_avt_3 -> {
                return R.drawable.ic_maker_sos_3
            }

            R.drawable.ic_avt_4 -> {
                return R.drawable.ic_maker_sos_4
            }

            R.drawable.ic_avt_5 -> {
                return R.drawable.ic_maker_sos_5
            }

            R.drawable.ic_avt_6 -> {
                return R.drawable.ic_maker_sos_6
            }

            R.drawable.ic_avt_7 -> {
                return R.drawable.ic_maker_sos_7
            }

            R.drawable.ic_avt_8 -> {
                return R.drawable.ic_maker_sos_8
            }

            R.drawable.ic_avt_9 -> {
                return R.drawable.ic_maker_sos_9
            }

            R.drawable.ic_avt_10 -> {
                return R.drawable.ic_maker_sos_10
            }

            R.drawable.ic_avt_11 -> {
                return R.drawable.ic_maker_sos_11
            }

            R.drawable.ic_avt_12 -> {
                return R.drawable.ic_maker_sos_12
            }

            R.drawable.ic_avt_13 -> {
                return R.drawable.ic_maker_sos_13
            }

            R.drawable.ic_avt_14 -> {
                return R.drawable.ic_maker_sos_14
            }

            R.drawable.ic_avt_15 -> {
                return R.drawable.ic_maker_sos_15
            }

            R.drawable.ic_avt_16 -> {
                return R.drawable.ic_maker_sos_16
            }

            R.drawable.ic_avt_17 -> {
                return R.drawable.ic_maker_sos_17
            }

            R.drawable.ic_avt_18 -> {
                return R.drawable.ic_maker_sos_18
            }
        }
        return R.drawable.ic_maker_sos_1
    }

    fun getActiveTime(time: Long?): String {
        val date = Date(time!!)
        val sdf = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault())
        return sdf.format(date)
    }

    @SuppressLint("DefaultLocale")
    fun getCoordinate(latLng: LatLng): String {
        var latitude = latLng.latitude
        var longitude = latLng.longitude
        val latDirection = if (latitude >= 0) "N" else "S"
        val lngDirection = if (longitude >= 0) "E" else "W"
        latitude = Math.abs(latitude)
        longitude = Math.abs(longitude)
        val latStr = String.format("%.6f", latitude)
        val lngStr = String.format("%.6f", longitude)
        return "$latStr°$latDirection, $lngStr°$lngDirection"
    }

    interface onSuccessListener {
        fun onSuccess()
    }

    interface LocationCallback {
        fun onLocationReceived(latLng: LatLng?)
    }
}

