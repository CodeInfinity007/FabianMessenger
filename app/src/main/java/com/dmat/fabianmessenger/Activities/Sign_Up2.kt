package com.dmat.fabianmessenger

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class Sign_Up2 : AppCompatActivity() {

    var num: Any? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2)

        val next_butt_su2: ImageButton = findViewById(R.id.next_butt_su2)
        val email_su2: EditText = findViewById(R.id.email_su2)
        val password_su2: EditText = findViewById(R.id.password_su2)
        val dp_upload: ImageButton = findViewById(R.id.dp_upload)
        //val circle_imgview: CircleImageView = findViewById(R.id.circle_imgview)

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
            ActivityCompat.requestPermissions(this, arrayOf(perm3), 25) }

        if (ContextCompat.checkSelfPermission(this, perm4) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(perm4), 7923) }

        if (ContextCompat.checkSelfPermission(this, perm5) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(perm5), 8923) }

        if (ContextCompat.checkSelfPermission(this, perm6) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(perm6), 81345) }

        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        num = telephonyManager.line1Number

        next_butt_su2.setOnClickListener {
            val email = email_su2.text.toString()
            val password = password_su2.text.toString()

            if (email.isEmpty() or password.isEmpty()) {
                Toast.makeText(this, "Invalid Entries", Toast.LENGTH_SHORT).show()
            } else {
                RegisterUser(email_su2, password_su2)
                uppload_dp()
                Toast.makeText(this, "User Successfully Created", Toast.LENGTH_SHORT).show()
            }

        }

        dp_upload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            startActivityForResult(intent, 0)
        }

    }

    var photoUri: Uri? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val dp_upload: ImageButton = findViewById(R.id.dp_upload)
        val circle_imgview: CircleImageView = findViewById(R.id.circle_imgview)

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 0 && data != null) {
            photoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            circle_imgview.setImageBitmap(bitmap)
            dp_upload.alpha = 0f
        }
    }

    private fun RegisterUser(email: EditText, password: EditText) {
        val email = email.text.toString()
        val password = password.text.toString() // pass = fab_pass

        if (email.isEmpty() or password.isEmpty()) {
            Toast.makeText(this, "Invalid Email/Password", Toast.LENGTH_SHORT).show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
            }
            .addOnFailureListener {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uppload_dp() {

        // Save Display Picture to Firebase Storage
        if (photoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(photoUri!!)
            .addOnSuccessListener {
                //Toast.makeText(this, "Image Uploaded: ${it.metadata?.path}", Toast.LENGTH_SHORT).show()
                ref.downloadUrl.addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener{
                        val profileURL = it.toString()
                        save_user(profileURL)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Not Uploaded${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun save_user(profileURL: String){
        val intent  = intent
        val username_string = intent.getStringExtra("Username")
        val name = intent.getStringExtra("Name")
        //Toast.makeText(this, "In here", Toast.LENGTH_SHORT).show()
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val typing_ref = FirebaseDatabase.getInstance().getReference("/users/$uid/isTyping")
        val user = User(uid, name!!, username_string!!, profileURL, num.toString(), true)

        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "User Saved to Firebase Server", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,  Messages::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//                intent.putExtra("selfuser", user)
                startActivity(intent)
            }
            .addOnFailureListener{
                Toast.makeText(this, "Not Uploaded${it.message}", Toast.LENGTH_SHORT).show()
            }

        val typ = isTyping(to = false, from = false, fromid = "", toUid = "")
        typing_ref.setValue(typ)
    }
}

