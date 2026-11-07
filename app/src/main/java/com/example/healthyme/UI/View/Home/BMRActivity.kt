package com.example.healthyme.UI.View.Home

import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.healthyme.R
import com.google.firebase.database.FirebaseDatabase

class BMRActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var usia: EditText
    private lateinit var beratBadan: EditText
    private lateinit var tinggiBadan: EditText
    private lateinit var radioPria: RadioButton
    private lateinit var radioWanita: RadioButton
    private lateinit var cekHasilButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bmractivity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle("Prediksi Kalori Harian")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Inisialisasi UI
        usia = findViewById(R.id.usia)
        beratBadan = findViewById(R.id.beratBadan)
        tinggiBadan = findViewById(R.id.tinggiBadan)
        radioPria = findViewById(R.id.radioPria)
        radioWanita = findViewById(R.id.radioWanita)
        cekHasilButton = findViewById(R.id.cekHasilButton)

        cekHasilButton.setOnClickListener {
            val usiaInput = usia.text.toString().toIntOrNull()
            val beratBadanInput = beratBadan.text.toString().toFloatOrNull()
            val tinggiBadanInput = tinggiBadan.text.toString().toFloatOrNull()
            val gender = if (radioPria.isChecked) "pria" else "wanita"

            if (usiaInput != null && beratBadanInput != null && tinggiBadanInput != null) {
                val bmr = calculateBMR(usiaInput, beratBadanInput, tinggiBadanInput, gender)

                // Ambil userId dari SharedPreferences
                val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
                val userId = sharedPreferences.getString("user_id", null)

                if (userId != null) {
                    showConfirmationDialog(userId, bmr, usiaInput, beratBadanInput, tinggiBadanInput, gender)
                } else {
                    Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Isi semua data dengan benar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun calculateBMR(age: Int, weight: Float, height: Float, gender: String): Float {
        // Formula BMR Mifflin-St Jeor
        return if (gender == "pria") {
            (10 * weight + 6.25 * height - 5 * age + 5).toFloat() // Casting ke Float
        } else {
            (10 * weight + 6.25 * height - 5 * age - 161).toFloat() // Casting ke Float
        }
    }

    private fun showConfirmationDialog(userId: String, bmr: Float, age: Int, weight: Float, height: Float, gender: String) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin ingin menyimpan data BMR ini?")
            .setPositiveButton("Ya") { dialogInterface: DialogInterface, i: Int ->
                // Menyimpan BMR ke SharedPreferences hanya jika BMR belum disimpan
                val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                // Periksa apakah BMR sudah disimpan sebelumnya
                if (sharedPreferences.getFloat("bmr", 0f) == 0f) {
                    editor.putFloat("bmr", bmr) // Menyimpan nilai BMR
                    editor.apply()
                }

                // Menyimpan data ke Firebase
                saveOrUpdateBMR(userId, bmr, age, weight, height, gender)
            }
            .setNegativeButton("Tidak") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun saveOrUpdateBMR(userId: String, bmr: Float, age: Int, weight: Float, height: Float, gender: String) {
        val database = FirebaseDatabase.getInstance().getReference("BMRResults")

        // Periksa apakah data BMR sudah ada
        database.child(userId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // Jika data BMR sudah ada, perbarui
                dataSnapshot.ref.child("bmr").setValue(bmr)
                dataSnapshot.ref.child("age").setValue(age)
                dataSnapshot.ref.child("weight").setValue(weight)
                dataSnapshot.ref.child("height").setValue(height)
                dataSnapshot.ref.child("gender").setValue(gender)
                    .addOnCompleteListener {
                        Toast.makeText(this, "BMR berhasil diperbarui", Toast.LENGTH_SHORT).show()

                        // Update SharedPreferences dengan BMR terbaru
                        val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putFloat("bmr", bmr)
                        editor.apply()

                        // Memuat ulang HomeFragment
                        val homeFragment = HomeFragment() // Buat instance baru dari HomeFragment
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.fragment_container, homeFragment) // Ganti dengan ID container fragment yang sesuai
                        transaction.addToBackStack(null) // Agar fragment bisa dinavigasi kembali
                        transaction.commit()

                        // Memanggil loadProfileData() di HomeFragment setelah fragment dimuat
                        homeFragment.loadProfileData()  // Pastikan untuk memanggil ini
                    }
            } else {
                // Jika data BMR belum ada, simpan data baru
                val newBMRData = mapOf(
                    "bmr" to bmr,
                    "age" to age,
                    "weight" to weight,
                    "height" to height,
                    "gender" to gender
                )
                database.child(userId).setValue(newBMRData)
                    .addOnCompleteListener {
                        Toast.makeText(this, "BMR berhasil disimpan", Toast.LENGTH_SHORT).show()

                        // Update SharedPreferences dengan BMR terbaru
                        val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putFloat("bmr", bmr)
                        editor.apply()

                        // Memuat ulang HomeFragment
                        val homeFragment = HomeFragment() // Buat instance baru dari HomeFragment
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.fragment_container, homeFragment) // Ganti dengan ID container fragment yang sesuai
                        transaction.addToBackStack(null) // Agar fragment bisa dinavigasi kembali
                        transaction.commit()

                        // Memanggil loadProfileData() di HomeFragment setelah fragment dimuat
                        homeFragment.loadProfileData()  // Pastikan untuk memanggil ini
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal mengakses database: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

}
