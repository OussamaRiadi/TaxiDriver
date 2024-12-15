package com.example.taxidriver

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.taxidriver.R
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class DriverFragment : Fragment() {

    private lateinit var editNom: EditText
    private lateinit var editPrenom: EditText
    private lateinit var editAge: EditText
    private lateinit var spinnerPermis: Spinner
    private lateinit var btnSave: Button
    private lateinit var textDisplay: TextView
    private lateinit var qrCodeImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_driver, container, false)

        // Initialize views
        editNom = view.findViewById(R.id.edit_nom)
        editPrenom = view.findViewById(R.id.edit_prenom)
        editAge = view.findViewById(R.id.edit_age)
        spinnerPermis = view.findViewById(R.id.spinner_permis)
        btnSave = view.findViewById(R.id.btn_save)
        textDisplay = view.findViewById(R.id.text_display)
        qrCodeImage = view.findViewById(R.id.qr_code_image)

        // Load saved data
        loadSavedData()

        // Save data and generate QR code on button click
        btnSave.setOnClickListener {
            saveData()
        }

        return view
    }

    private fun saveData() {
        val nom = editNom.text.toString().trim()
        val prenom = editPrenom.text.toString().trim()
        val age = editAge.text.toString().trim()
        val permis = spinnerPermis.selectedItem.toString().trim()

        // Validate input
        if (nom.isEmpty() || prenom.isEmpty() || age.isEmpty()) {
            Toast.makeText(context, "Veuillez remplir tous les champs!", Toast.LENGTH_SHORT).show()
            return
        }

        if (!age.matches(Regex("\\d+"))) {
            Toast.makeText(context, "Âge doit être un nombre valide!", Toast.LENGTH_SHORT).show()
            return
        }

        // Save data to SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("DriverPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("Nom", nom)
            putString("Prenom", prenom)
            putString("Age", age)
            putString("Permis", permis)
            apply()
        }

        // Display saved data
        textDisplay.text = "Nom: $nom\nPrénom: $prenom\nÂge: $age\nType de permis: $permis"

        // Generate QR code
        generateQRCode("Nom: $nom\nPrénom: $prenom\nÂge: $age\nType de permis: $permis")

        Toast.makeText(context, "Données enregistrées avec succès!", Toast.LENGTH_SHORT).show()
    }

    private fun loadSavedData() {
        val sharedPreferences = requireContext().getSharedPreferences("DriverPrefs", Context.MODE_PRIVATE)
        val nom = sharedPreferences.getString("Nom", "")
        val prenom = sharedPreferences.getString("Prenom", "")
        val age = sharedPreferences.getString("Age", "")
        val permis = sharedPreferences.getString("Permis", "")

        // Pre-fill fields with saved data
        editNom.setText(nom)
        editPrenom.setText(prenom)
        editAge.setText(age)

        // Display saved data
        if (!nom.isNullOrEmpty() && !prenom.isNullOrEmpty() && !age.isNullOrEmpty()) {
            textDisplay.text = "Nom: $nom\nPrénom: $prenom\nÂge: $age\nType de permis: $permis"
            generateQRCode("Nom: $nom\nPrénom: $prenom\nÂge: $age\nType de permis: $permis")
        }
    }

    private fun generateQRCode(data: String) {
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(
                data,
                BarcodeFormat.QR_CODE,
                400,
                400
            )
            qrCodeImage.setImageBitmap(bitmap)
            qrCodeImage.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erreur lors de la génération du QR code.", Toast.LENGTH_SHORT).show()
        }
    }
}
