package com.example.taxidriver

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.taxidriver.databinding.FragmentMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlin.math.*

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionCode = 100
    private var lastLocation: LatLng? = null
    private var totalDistance = 0.0 // In kilometers
    private var timeElapsedInSeconds = 0 // Total elapsed time

    private val baseFare = 2.5 // Base fare in DH
    private val perKmFare = 1.5 // Fare per kilometer
    private val perMinuteFare = 0.5 // Fare per minute

    private val totalFare = MutableLiveData(0.0)
    private val isRideActive = MutableLiveData(false)
    private var rideJob: Job? = null

    private val currentDistance = MutableLiveData(0.0) // Distance in kilometers
    private val currentTime = MutableLiveData("00:00") // Time in MM:SS format

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().window.apply {
                // Set status bar color to yellow
                statusBarColor = ContextCompat.getColor(requireContext(), R.color.yellow)
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupObservers()

        // Button toggle action
        binding.buttonToggleRide.setOnClickListener { toggleRide() }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
    }

    private fun setupObservers() {
        totalFare.observe(viewLifecycleOwner) { fare ->
            binding.textTotalFare.text = "Tarif total : ${"%.2f".format(fare)} DH"
        }

        currentDistance.observe(viewLifecycleOwner) { distance ->
            binding.textDistance.text = "Distance : ${"%.2f".format(distance)} km"
        }

        currentTime.observe(viewLifecycleOwner) { time ->
            binding.textTimeElapsed.text = "Temps : $time"
        }

        isRideActive.observe(viewLifecycleOwner) { active ->
            binding.buttonToggleRide.text = if (active) "Stop Ride" else "Start Ride"
            binding.buttonToggleRide.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (active) R.color.red else R.color.green
                )
            )
        }
    }

    private fun toggleRide() {
        if (isRideActive.value == true) {
            showStopRideConfirmation()
        } else {
            startRide()
        }
    }

    private fun showStopRideConfirmation() {
        AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to stop the ride?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ -> stopRide() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun startRide() {
        isRideActive.value = true
        timeElapsedInSeconds = 0
        totalDistance = 0.0
        totalFare.value = baseFare

        rideJob = lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                while (isRideActive.value == true) {
                    delay(1000) // Update every second
                    timeElapsedInSeconds++

                    // Update fare based on time and distance
                    val distanceFare = totalDistance * perKmFare
                    val timeFare = (timeElapsedInSeconds / 60.0) * perMinuteFare
                    totalFare.postValue(baseFare + distanceFare + timeFare)

                    // Update the time in MM:SS format
                    val minutes = timeElapsedInSeconds / 60
                    val seconds = timeElapsedInSeconds % 60
                    currentTime.postValue(String.format("%02d:%02d", minutes, seconds))
                }
            }
        }

        startLocationUpdates()
    }

    private fun stopRide() {
        isRideActive.value = false
        rideJob?.cancel()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 1000 // 1 second
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
            val location = result.lastLocation ?: return
            val currentLatLng = LatLng(location.latitude, location.longitude)

            if (lastLocation != null) {
                val distance = calculateDistance(lastLocation!!, currentLatLng)
                totalDistance += distance
                currentDistance.postValue(totalDistance) // Update live distance
                Log.d("DistanceUpdate", "Added distance: $distance, Total: $totalDistance")
            }

            lastLocation = currentLatLng
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
        }
    }

    private fun calculateDistance(start: LatLng, end: LatLng): Double {
        val radius = 6371.0 // Earth's radius in km
        val latDiff = Math.toRadians(end.latitude - start.latitude)
        val lonDiff = Math.toRadians(end.longitude - start.longitude)
        val a = sin(latDiff / 2).pow(2) +
                cos(Math.toRadians(start.latitude)) *
                cos(Math.toRadians(end.latitude)) *
                sin(lonDiff / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radius * c // Distance in km
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
