package com.example.geofencing

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng

class GeofenceHelper(base: Context?) : ContextWrapper(base) {

    private var pendingIntent: PendingIntent? = null

    fun getGeofencingRequest(geofence: Geofence): GeofencingRequest {
        val geofenceRequest = GeofencingRequest.Builder()
            .addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .build()
        Log.d(TAG, "Build geofence: " + geofenceRequest.initialTrigger)
        return geofenceRequest
    }

    fun getGeofence(ID: String, latLng: LatLng, radius: Float, transitionTypes: Int): Geofence {
        val geofence = Geofence.Builder()
            .setCircularRegion(latLng.latitude,latLng.latitude, radius)
            .setRequestId(ID)
            .setTransitionTypes(transitionTypes)
            .setLoiteringDelay(5000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
        Log.d(TAG, "get Geofence " + geofence.requestId)
        return geofence
    }

    fun getPendingIntent(): PendingIntent {
        val testIntent = Intent(this, GeofenceBroadcastReceiver::class.java)
        Log.d(TAG, "Test send broadcast")
        sendBroadcast(testIntent)

        if (pendingIntent != null) {
            Log.d(TAG, "pendingIntent != null : " + pendingIntent.toString())
            return pendingIntent!!
        }
        val intent: Intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        Log.d(TAG, "setting pendingIntent : " + pendingIntent.toString())
        return pendingIntent!!
//        // ตรวจสอบว่า pendingIntent เป็น null หรือไม่
//        if (pendingIntent == null) {
//            // ถ้าเป็น null ให้สร้างใหม่
//            val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
//            pendingIntent = PendingIntent.getBroadcast(this, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//        }
//        // คืนค่ากลับเป็น pendingIntent โดยไม่ต้องใช้ smart cast
//        return pendingIntent!!
    }

    fun getErrorString(e: Exception): String {
        if (e is ApiException) {
            val apiExeption: ApiException = e as ApiException
            when (apiExeption.getStatusCode()) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> return "GEOFENCE_NOT_AVAILABLE"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> return "GEOFENCE_TOO_MANY_GEOFENCES"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> return "GEOFENCE_TOO_MANY_PENDING_INTENTS"
            }
        }
        return e.getLocalizedMessage()
    }

    companion object {
        private const val TAG = "GeofenceHelper"
    }

}