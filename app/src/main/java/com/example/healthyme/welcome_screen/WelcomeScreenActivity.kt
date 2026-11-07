package com.example.healthyme.welcome_screen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.healthyme.R
import com.example.healthyme.UI.View.LoginActivity
import com.example.healthyme.databinding.ActivityWelcomeScreenBinding
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class WelcomeScreenActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var btnSkip: TextView
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewPager = findViewById(R.id.viewPager)
        dotsIndicator = findViewById(R.id.dotsIndicator)
        btnSkip = findViewById(R.id.btnSkip)
        btnNext = findViewById(R.id.btnNext)

        val fragmentList = listOf(Welcome1Fragment(), Welcome2Fragment(), Welcome3Fragment())
        val adapter = PageAdapter(this, fragmentList)

        viewPager.adapter = adapter

        dotsIndicator.attachTo(viewPager)

        btnNext.setOnClickListener {
            if (viewPager.currentItem < fragmentList.size -1) {
                viewPager.currentItem += 1
            } else {
                finishWelcomeScreen()
            }
        }

        btnSkip.setOnClickListener {
            finishWelcomeScreen()
        }

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if(position == fragmentList.size -1) {
                    btnNext.text = "Finish"
                    btnSkip.visibility = View.GONE
                } else {
                    btnNext.text = "Next"
                    btnSkip.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun finishWelcomeScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}