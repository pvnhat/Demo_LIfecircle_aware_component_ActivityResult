package com.example.lifecircleawaredemo

import android.content.*
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class LocationLifeCircleObserver(
    private val onReceiveLocation: (location: Location) -> Unit,
    private val context: Context
) : LifecycleObserver {

    private var locationUpdatesService: LocationUpdatesService? = null
    private var isBounding = false
    private lateinit var locationReceiver: LocationReceiver
    private var isTrackingLocation = false

    private var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("ccccc", "onServiceConnected")
            locationUpdatesService = (service as LocationUpdatesService.LocalBinder).getService()
            isBounding = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationUpdatesService = null
            isBounding = false
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun registerLocationListener() {
        //getContent = registry.register()

        Log.d("ccccc", "ON_CREATE")
        locationReceiver = LocationReceiver {
            onReceiveLocation(it)
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(
            locationReceiver,
            IntentFilter(UPDATE_LOCATION_BROADCAST)
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun startLocationListener() {
        Log.d("ccccc", "ON_START")
        context.bindService(
            Intent(context, LocationUpdatesService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    fun turnLocationUpdate(){
        if (isTrackingLocation) removeLocationUpdate()
        else requestLocationUpdate()
    }

    private fun requestLocationUpdate() {
        Log.d("ccccc", "requestLocationUpdate $locationUpdatesService")
        isTrackingLocation = true
        locationUpdatesService?.requestLocationUpdate()
    }

    private fun removeLocationUpdate() {
        Log.d("ccccc", "removeLocationUpdate")
        isTrackingLocation = false
        locationUpdatesService?.removeLocationUpdate()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun unListenLocation(){
        Log.d("ccccc", "ON_STOP")
        if (isBounding) {
            context.unbindService(serviceConnection)
            isBounding = !isBounding
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun releaseListen(){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(locationReceiver)
        locationUpdatesService?.removeLocationUpdate()
    }

}
