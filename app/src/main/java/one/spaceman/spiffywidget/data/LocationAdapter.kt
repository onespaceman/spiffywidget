package one.spaceman.spiffywidget.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.Locale
import kotlin.coroutines.resume

object LocationAdapter {
    suspend fun get(
        context: Context, locationClient: FusedLocationProviderClient
    ): Location? {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val location = locationClient.lastLocation.await()
            return location
        } else return null
    }

    suspend fun geocode(
        context: Context, location: Location
    ): String? {
        try {
            val address = getAddress(context, location)
            return address?.locality
        } catch (_: Exception) {
            return null
        }
    }

    private suspend fun getAddress(
        context: Context,
        location: Location,
    ): Address? = suspendCancellableCoroutine { continuation ->

        val geocoder = Geocoder(context, Locale.getDefault())

        geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            1,
            object : Geocoder.GeocodeListener {

                override fun onGeocode(addresses: MutableList<Address>) {
                    continuation.resume(addresses.firstOrNull())
                }

                override fun onError(errorMessage: String?) {
                    continuation.resume(null)
                }
            }
        )
    }
}