package com.dmat.fabianmessenger.Activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.dmat.fabianmessenger.R
import com.dmat.fabianmessenger.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings_chats.*

class Settings_chats : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_chats)

        val actionBar = supportActionBar
        actionBar?.elevation = 0f
        actionBar?.title = "Chats"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_home_indicator)

        var selfuser: User? = null
        var selfuser1: User? = null

        val selfuid = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$selfuid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                selfuser = snapshot.getValue(User::class.java)
//                compressImg = selfuser?.compressImg

                compressimg_settings_chats.isChecked = selfuser?.compressImg!!
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        compressimg_settings_chats.setOnClickListener {
            Toast.makeText(this@Settings_chats, "Clicked", Toast.LENGTH_SHORT).show()

            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    selfuser1 = snapshot.getValue(User::class.java)
                    selfuser1?.compressImg = compressimg_settings_chats.isChecked
                    ref.setValue(selfuser1)
                    Toast.makeText(this@Settings_chats, compressimg_settings_chats.isChecked.toString(), Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@Settings_chats, selfuser1?.compressImg.toString(), Toast.LENGTH_SHORT).show()

//                    compressimg_settings_chats.isChecked = selfuser?.compressImg!!
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        }

    }
}