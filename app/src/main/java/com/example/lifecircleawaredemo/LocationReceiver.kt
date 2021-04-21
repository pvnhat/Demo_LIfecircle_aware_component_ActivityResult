package com.example.lifecircleawaredemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.widget.Toast

class LocationReceiver(private val onReceiver: (location: Location) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.getParcelableExtra<Location>(EXTRA_LOCATION)?.let { location ->
            onReceiver(location)
        }
    }
}