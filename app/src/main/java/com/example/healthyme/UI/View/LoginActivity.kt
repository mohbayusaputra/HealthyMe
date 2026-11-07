package com.example.healthyme.UI.View

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthyme.R
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameField = findViewById<EditText>(R.id.username)
        val emailField = findViewById<EditText>(R.id.email)
        val passwordField = findViewById<EditText>(R.id.password)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val backToRegister = findViewById<TextView>(R.id.backToRegister)

        btnLogin.setOnClickListener {
            val username = usernameField.text.toString()
            val email = emailField.text.toString()  // Ambil email
            val password = passwordField.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            } else {
                val database = FirebaseDatabase.getInstance().getReference("Users")
                database.get().addOnSuccessListener { dataSnapshot ->
                    var loginSuccessful = false
                    for (userSnapshot in dataSnapshot.children) {
                        val storedUsername = userSnapshot.child("username").value.toString()
                        val storedEmail = userSnapshot.child("email").value.toString()
                        val storedPassword = userSnapshot.child("password").value.toString()
                        val userId = userSnapshot.key // Ambil userId

                        if (storedUsername == username && storedEmail == email && storedPassword == password) {
                            loginSuccessful = true

                            // Simpan userId, username, dan email di SharedPreferences
                            val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("user_id", userId)  // Simpan userId
                            editor.putString("username", username)
                            editor.putString("email", email)
                            editor.apply()

                            // Kirimkan username dan email ke MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("USERNAME", username)  // Kirimkan username
                            intent.putExtra("EMAIL", email)  // Kirimkan email
                            startActivity(intent)
                            finish()  // Tutup LoginActivity
                            break
                        }
                    }
                    if (!loginSuccessful) {
                        Toast.makeText(this, "Invalid username or password!", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Database error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        backToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}