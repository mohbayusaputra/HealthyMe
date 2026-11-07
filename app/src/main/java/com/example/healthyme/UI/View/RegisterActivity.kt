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

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val usernameField = findViewById<EditText>(R.id.username)
        val emailField = findViewById<EditText>(R.id.email)
        val passwordField = findViewById<EditText>(R.id.password)
        val confirmPasswordField = findViewById<EditText>(R.id.confirm_password)
        val btnRegister = findViewById<Button>(R.id.btn_register)
        val backToLogin = findViewById<TextView>(R.id.backToLogin)

        btnRegister.setOnClickListener {
            val username = usernameField.text.toString()
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Isi semua inputan!", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Password tidak sama!", Toast.LENGTH_SHORT).show()
            } else {
                val database = FirebaseDatabase.getInstance().getReference("Users")
                val userId = database.push().key // Generate unique ID

                if (userId != null) {
                    val userMap = mapOf(
                        "username" to username,
                        "email" to email,
                        "password" to password
                    )
                    database.child(userId).setValue(userMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Register berhasil!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Database error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        backToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}