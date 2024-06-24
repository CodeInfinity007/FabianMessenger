package com.dmat.fabianmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sign_up_butt: ImageButton = findViewById(R.id.sign_up_butt)
        val next_butt_main: ImageButton = findViewById(R.id.next_butt_main)
        val email: EditText = findViewById(R.id.email)
        val password: EditText = findViewById(R.id.password)

        sign_up_butt.setOnClickListener {
            val Intent = Intent(this, Sign_Up1::class.java)
            startActivity(Intent)
        }
        next_butt_main.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(this, "Succesfully Logged In", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Messages::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener{
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

            val intent = Intent(this,  Messages::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


    }


}



