package com.dmat.fabianmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast

class Sign_Up1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up1)

        val next_butt_su1: ImageButton = findViewById(R.id.next_butt_su1)
        val name: EditText = findViewById(R.id.name)
        val username: EditText = findViewById(R.id.username)

        next_butt_su1.setOnClickListener {
            val name = name.text.toString()
            val username_string = username.text.toString()

            if (name.isEmpty() or username_string.isEmpty()) {
                Toast.makeText(this, "Invalid Entries", Toast.LENGTH_SHORT).show()
            } else {
                val Intent = Intent(this, Sign_Up2::class.java)
                Intent.putExtra("Username", username_string)
                Intent.putExtra("Name", name)
                startActivity(Intent)
            }


        }
    }
}