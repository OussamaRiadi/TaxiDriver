package com.example.taxidriver

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

class CounterFragment : Fragment(R.layout.fragment_counter) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isRideActive = false
    private var totalDistance = 0f // in meters
    private var totalTime = 0L // in seconds
    private var lastLocation: android.location.Location? = null // Use android.location.Location here
    private val baseFare = 2.5
    private val farePerKm = 1.5
    private val farePerMinute = 0.5
    private lateinit var handler: Handler
    private lateinit var updateTimeRunnable: Runnable

    // Define a unique channel ID for notifications
    private val CHANNEL_ID = "taxi_ride_channel"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textTotalFare: TextView = view.findViewById(R.id.text_total_fare)
        val buttonStart: Button = view.findViewById(R.id.button_start)
        val buttonStop: Button = view.findViewById(R.id.button_stop)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        handler = Handler(Looper.getMainLooper())

        // Create notification channel
        createNotificationChannel()

        // Start Ride Logic
        buttonStart.setOnClickListener {
            if (!isRideActive) {
                isRideActive = true
                totalDistance = 0f
                totalTime = 0L
                lastLocation = null
                startLocationUpdates()
                startTimer()

                textTotalFare.text = "Tarif total : 0 DH"
                buttonStart.isEnabled = false
                buttonStop.isEnabled = true

                // Show notification when the ride starts
                showRideNotification("Taxi Ride Started", "Your ride has started!")
            }
        }

        // Stop Ride Logic
        buttonStop.setOnClickListener {
            if (isRideActive) {
                isRideActive = false
                stopLocationUpdates()
                stopTimer()
                buttonStart.isEnabled = true
                buttonStop.isEnabled = false

                // Show notification when the ride stops
                showRideNotification("Taxi Ride Stopped", "Your ride has stopped. Fare calculation completed.")
            }
        }

        buttonStop.isEnabled = false // Disable stop button initially
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Taxi Ride Notifications"
            val descriptionText = "Notifications for taxi ride start/stop"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showRideNotification(title: String, message: String) {
        val notificationManager: NotificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification: Notification = Notification.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.taxi)  // Replace with your own icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(Notification.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Automatically dismiss after user taps
            .build()

        notificationManager.notify(1, notification)
    }

    private fun startTimer() {
        updateTimeRunnable = object : Runnable {
            override fun run() {
                totalTime++
                calculateFare()
                handler.postDelayed(this, 1000) // Update every second
            }
        }
        handler.post(updateTimeRunnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(updateTimeRunnable)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(5000) // 5 seconds interval
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (isRideActive) {
                for (location in locationResult.locations) {
                    if (lastLocation != null) {
                        totalDistance += lastLocation!!.distanceTo(location) // Calculate distance
                    }
                    lastLocation = location // Assign android.location.Location
                }
                calculateFare()
            }
        }
    }

    private fun calculateFare() {
        val distanceKm = totalDistance / 1000 // Convert meters to kilometers
        val timeMinutes = totalTime / 60.0 // Convert seconds to minutes

        val totalFare = baseFare + (distanceKm * farePerKm) + (timeMinutes * farePerMinute)
        val textTotalFare: TextView? = view?.findViewById(R.id.text_total_fare)
        textTotalFare?.text = String.format("Tarif total : %.2f DH", totalFare)
    }
}
