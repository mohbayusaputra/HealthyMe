package com.example.healthyme.UI.View.Profil

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.healthyme.R
import com.google.firebase.database.FirebaseDatabase

class EditProfileActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var usernameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle("Edit Profile")
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Inisialisasi
        usernameEdit = findViewById(R.id.username)
        emailEdit = findViewById(R.id.email)
        passwordEdit = findViewById(R.id.password)
        btnSave = findViewById(R.id.btn_save)

        // Ambil userId dan data lainnya dari SharedPreferences untuk menampilkan data saat ini
        val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null) // Ambil userId
        val username = sharedPreferences.getString("username", "Unknown")
        val email = sharedPreferences.getString("email", "Unknown")
        val password = sharedPreferences.getString("password", "Unknown")

        usernameEdit.setText(username)
        emailEdit.setText(email)
        passwordEdit.setText(password)

        // logika untuk button save
        btnSave.setOnClickListener {
            val newUsername = usernameEdit.text.toString()
            val newEmail = emailEdit.text.toString()
            val newPassword = passwordEdit.text.toString()

            if (newUsername.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
            } else {
                if (userId != null) {
                    updateProfile(userId, newUsername, newEmail, newPassword)
                } else {
                    Toast.makeText(this, "User tidak ditemukan!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateProfile(userId: String, newUsername: String, newEmail: String, newPassword: String) {
        val database = FirebaseDatabase.getInstance().getReference("Users")

        // Update data berdasarkan userId login
        database.child(userId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // Update username, email, dan password
                dataSnapshot.ref.child("username").setValue(newUsername)
                dataSnapshot.ref.child("email").setValue(newEmail)
                dataSnapshot.ref.child("password").setValue(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Jika update berhasil
                            Toast.makeText(this@EditProfileActivity, "Profile berhasil diperbarui!", Toast.LENGTH_SHORT).show()

                            // Update SharedPreferences dengan data baru
                            val editor = getSharedPreferences("user_preferences", MODE_PRIVATE).edit()
                            editor.putString("username", newUsername)
                            editor.putString("email", newEmail)
                            editor.putString("password", newPassword) // Jika Anda ingin menyimpan password juga
                            editor.apply()

                            finish()  // Kembali ke halaman profil
                        } else {
                            // Jika terjadi error saat update
                            Toast.makeText(this@EditProfileActivity, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Jika pengguna tidak ditemukan
                Toast.makeText(this@EditProfileActivity, "Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this@EditProfileActivity, "Database error: ${it.message}", Toast.LENGTH_SHORT).show()
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
}
