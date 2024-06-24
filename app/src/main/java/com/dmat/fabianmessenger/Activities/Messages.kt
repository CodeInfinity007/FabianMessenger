package com.dmat.fabianmessenger

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dmat.fabianmessenger.Activities.Settings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.messages_header.*

class Messages : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    var num: Any? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

//        Firebase.database.setPersistenceEnabled(true)


        val perm1 = Manifest.permission.CAMERA
        val perm2 = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val perm3 = Manifest.permission.READ_EXTERNAL_STORAGE
        val perm4 = Manifest.permission.READ_SMS
        val perm5 = Manifest.permission.READ_PHONE_NUMBERS
        val perm6 = Manifest.permission.READ_PHONE_STATE


        if (ContextCompat.checkSelfPermission(this, perm1) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(perm1), 45)
        }
        if (ContextCompat.checkSelfPermission(this, perm2) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(perm2), 24)
        }
        if (ContextCompat.checkSelfPermission(this, perm3) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(perm3), 25)
        }
        if (ContextCompat.checkSelfPermission(this, perm4) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(perm4), 7923)
        }
        if (ContextCompat.checkSelfPermission(this, perm5) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(perm5), 8923)
        }
        if (ContextCompat.checkSelfPermission(this, perm6) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(perm6), 81345)
        }

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.messages_header)

        recyclerview_latestmsg.adapter = adapter

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, chat_log::class.java)
            val row = item as LatestMessageRow
            intent.putExtra("user", row.chatPartnerUser)
            startActivity(intent)
        }

        adapter.setOnItemLongClickListener { item, view ->
            val popup = PopupMenu(this, view)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_delete -> {
                        makeToast("Delete"); true
                    }
                    else -> {
                        false
                    }
                }
            }
            popup.inflate(R.menu.latmsg_menu)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popup.setForceShowIcon(true)
            }
            popup.show();true
        }

        new_msg_newmsg.setOnClickListener {
            val intent = Intent(this, NewMessage::class.java)
            startActivity(intent)
        }
        more_newmsg.setOnClickListener {
            val popup2 = PopupMenu(this, it)
            popup2.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_settings -> {
                        val intent = Intent(this, Settings::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.menu_signout -> {
                        showsignout_dialog()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
            popup2.inflate(R.menu.menu)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popup2.setForceShowIcon(true)
            }
            popup2.show()
        }

        verifyUserLogin()
        //setUpDummyChats()
        RecieveLatestMessages()
    }

    private fun makeToast(text: String) {
        return Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    val latestMsgMap = HashMap<String, ChatMessage>()

    private fun refreshMessages() {
        adapter.clear()
        latestMsgMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun RecieveLatestMessages() {
        val toid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$toid")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                latestMsgMap[snapshot.key!!] = chatMessage!!
                refreshMessages()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                latestMsgMap[snapshot.key!!] = chatMessage!!
                refreshMessages()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        })
    }


    private fun verifyUserLogin() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    private fun showsignout_dialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_conformation_signout)

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        val cancel = dialog.findViewById<Button>(R.id.cancel_dialog_signout)
        val signout= dialog.findViewById<Button>(R.id.signout_dialog)

        cancel.setOnClickListener { dialog.dismiss() }
        signout.setOnClickListener {
            dialog.dismiss()

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }
    }
}