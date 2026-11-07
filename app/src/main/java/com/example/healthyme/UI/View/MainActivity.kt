package com.example.healthyme.UI.View

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.healthyme.R
import com.example.healthyme.UI.View.Favorit.FavoritFragment
import com.example.healthyme.UI.View.Home.HomeFragment
import com.example.healthyme.UI.View.News.NewsFragment
import com.example.healthyme.UI.View.Prediksi.PrediksiFragment
import com.example.healthyme.UI.View.Profil.ProfilFragment
import com.example.healthyme.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        loadFragment(HomeFragment())

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                }
                R.id.uji_ml -> {
                    loadFragment(PrediksiFragment())
                }
                R.id.favorit -> {
                    loadFragment(FavoritFragment())
                }
                R.id.news -> {
                    loadFragment(NewsFragment())
                }
                R.id.profil -> {
                    loadFragment(ProfilFragment())
                }
            }
            true
        }
    }
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}