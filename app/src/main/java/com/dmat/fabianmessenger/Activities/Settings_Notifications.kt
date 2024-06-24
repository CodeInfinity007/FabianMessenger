package com.dmat.fabianmessenger.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dmat.fabianmessenger.R

class Settings_Notifications : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_notifications)

        val actionBar = supportActionBar
        actionBar?.elevation = 0f
        actionBar?.title = "Notifications"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_home_indicator)

    }
}