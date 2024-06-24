package com.dmat.fabianmessenger.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dmat.fabianmessenger.R
import kotlinx.android.synthetic.main.activity_settings_help.*

class Settings_help : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_help)

        val actionBar = supportActionBar
        actionBar?.elevation = 0f
        actionBar?.title = "Settings"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_home_indicator)

        contactus_settings.setOnClickListener {
            val intent = Intent(this, Settings_Contact_us::class.java)
            startActivity(intent)
        }

    }
}