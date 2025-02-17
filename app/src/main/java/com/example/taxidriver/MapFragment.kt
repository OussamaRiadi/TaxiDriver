package com.example.taxidriver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MapFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var taxiCounter: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        // Reference to the Taxi Counter
        taxiCounter = rootView.findViewById(R.id.taxiCounter)

        // Hide the Taxi Counter after 5 seconds
        hideTaxiCounterAfterDelay()

        return rootView
    }

    private fun hideTaxiCounterAfterDelay() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(5000) // Wait for 5 seconds
            taxiCounter?.animate()
                ?.alpha(0f) // Fade out animation
                ?.setDuration(500) // Animation duration
                ?.withEndAction {
                    taxiCounter?.visibility = View.GONE // Hide the layout
                }
        }
    }

    fun showTaxiCounter() {
        taxiCounter?.apply {
            visibility = View.VISIBLE
            alpha = 0f
            animate()
                .alpha(1f) // Fade-in animation
                .setDuration(500)
                .start()
        }
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
