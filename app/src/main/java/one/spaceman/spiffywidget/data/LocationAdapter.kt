package one.spaceman.spiffywidget.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.tasks.await

object LocationAdapter {
    suspend fun get(
        context: Context,
        locationClient: FusedLocationProviderClient
    ): Location? {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        ) {

            val location = locationClient.lastLocation.await()
            return location
        } else return null
    }

    fun geocode(
        context: Context,
        location: Location
    ): Address? {
        val address = Geocoder(context).getFromLocation(location.latitude, location.longitude, 1)
        return if (!address.isNullOrEmpty()) {
            address.first()
        } else null
    }
}