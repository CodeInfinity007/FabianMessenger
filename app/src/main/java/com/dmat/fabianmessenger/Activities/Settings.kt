package com.dmat.fabianmessenger.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dmat.fabianmessenger.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_settings.*

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val actionBar = supportActionBar
        actionBar?.elevation = 0f
        actionBar?.title = "Settings"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_home_indicator)


        var selfuser: User? = null

        val selfuid = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$selfuid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Toast.makeText(this@Settings, "In Here", Toast.LENGTH_SHORT).show()
                selfuser = snapshot.getValue(User::class.java)

                name_settings.text = selfuser?.name
                phone_settings.text = selfuser?.phone_num
                Picasso.get().load(selfuser?.dp_url).into(default_dp_settings)
                Log.d("USERBIRD", selfuser?.dp_url.toString())
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        editprofile_settings.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("selfuser", selfuser)
            startActivity(intent)
        }
        acc_settings_layout.setOnClickListener {
            val intent = Intent(this, Settings_account::class.java)
            startActivity(intent)
        }
        chats_settings_layout.setOnClickListener {
            val intent = Intent(this, Settings_chats::class.java)
            startActivity(intent)
        }
        notification_settings_layout.setOnClickListener {
            val intent = Intent(this, Settings_Notifications::class.java)
            startActivity(intent)
        }
        help_settings_layout.setOnClickListener {
            val intent = Intent(this, Settings_help::class.java)
            startActivity(intent)
        }

    }
}