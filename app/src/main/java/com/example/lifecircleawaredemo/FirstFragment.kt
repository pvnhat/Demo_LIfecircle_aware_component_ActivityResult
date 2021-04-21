package com.example.lifecircleawaredemo

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.lifecircleownerdemo.R

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private lateinit var ivImage: ImageView
    private lateinit var locationLifecycleObserver: LocationLifeCircleObserver

    // Request multiple permissions contract
    private val requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                // Do something if some permissions granted or denied
                permissions.entries.forEach {
                    if (it.key == Manifest.permission.ACCESS_FINE_LOCATION && it.value) {
                        Log.d("ccccc"," ACCESS_FINE_LOCATION is granred")
                        locationLifecycleObserver.turnLocationUpdate()
                        return@forEach
                    }
                }
            }

    // Get content (image) result
    private val getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                ivImage.setImageURI(uri)
            }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_first, container, false)
        ivImage = view.findViewById(R.id.ivImage)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }


        locationLifecycleObserver = LocationLifeCircleObserver({
            Toast.makeText(
                    requireContext(),
                    "Location (${it.latitude} - ${it.longitude})",
                    Toast.LENGTH_SHORT
            ).show()
        }, requireContext())
        viewLifecycleOwner.lifecycle.addObserver(locationLifecycleObserver)

        ivImage.setOnClickListener {
            getContent.launch("image/*")
        }

        view.findViewById<Button>(R.id.btnRequest).setOnClickListener {
            if (!checkPermission()) requestMultiplePermissions.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            else locationLifecycleObserver.turnLocationUpdate()

        }
    }

    private fun checkPermission(): Boolean =
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
}