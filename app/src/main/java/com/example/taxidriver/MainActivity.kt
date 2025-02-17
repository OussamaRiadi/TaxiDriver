package com.example.taxidriver

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    // Request codes for permissions
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        // Set up toolbar
        setSupportActionBar(toolbar)

        // Set up navigation drawer toggle
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Set navigation listener
        navigationView.setNavigationItemSelectedListener(this)

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            navigationView.setCheckedItem(R.id.nav_map)
        }

        // Check and request location permission for map
        checkLocationPermission()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_map -> loadFragment(HomeFragment()) // Load HomeFragment
            R.id.nav_details -> loadFragment(DriverFragment()) // Load DriverFragment
            R.id.nav_logout -> logoutUser() // Handle logout
        }

        // Close the navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Function to dynamically load fragments with animations
    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in, // Animation when fragment enters
                android.R.anim.fade_out // Animation when fragment leaves
            )
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Function to handle user logout with Snackbar
    private fun logoutUser() {
        // Clear session data
        val sharedPreferences = getSharedPreferences("SessionPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut()

        // Show logout message using Snackbar
        Snackbar.make(findViewById(R.id.drawer_layout), "Logged out successfully!", Snackbar.LENGTH_SHORT).show()

        // Redirect to SignInActivity
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Finish MainActivity to remove it from the back stack
        finish()
    }

    // Function to check for location permission
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    // Handle the result of permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location-related tasks
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
