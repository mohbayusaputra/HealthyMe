package com.example.healthyme.UI.View.Profil

import android.app.Activity

data class ListActivityModel(
    val image: Int,
    val name: String,
    val activity: Class<out Activity>?
)