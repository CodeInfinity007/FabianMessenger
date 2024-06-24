package com.dmat.fabianmessenger.Activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import com.dmat.fabianmessenger.Models.HelpCentreModel
import com.dmat.fabianmessenger.R
import com.dmat.fabianmessenger.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_settings_contact_us.*
import java.io.ByteArrayOutputStream
import java.util.*

class Settings_Contact_us : AppCompatActivity() {
    var img_uri: Uri? = null
    var ToUid: String? = null
    var img_url: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_contact_us)

        val actionBar = supportActionBar
        actionBar?.elevation = 0f
        actionBar?.title = "Contact Us"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_home_indicator)

        ToUid = FirebaseAuth.getInstance().uid

        fab_save_contact_us.setOnClickListener {
            if (detail_problem_contact_us.length() <= 20) {
                detail_problem_contact_us.setBackgroundResource(R.drawable.edittext_error)
                error_msg_contact_us.visibility = View.VISIBLE
                return@setOnClickListener
            }
            val ref = FirebaseDatabase.getInstance().getReference("Help-Center/$ToUid").push()
            val helpmsg = HelpCentreModel(ref.key!!, detail_problem_contact_us.text.toString(), img_url, email_contact_us.text.toString(), ToUid.toString())
            ref.setValue(helpmsg)
            showconfodialog()
        }

        add_ss_contact_us.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

    }

    private fun showconfodialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.contact_us_confirmation_popup)

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        val ok_button = dialog.findViewById<Button>(R.id.ok_contact_us)
        ok_button.setOnClickListener {dialog.dismiss();finish()}

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 0 && data != null) {
            val photoUri = data.data
            val bytes = ByteArrayOutputStream()
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, bytes)
            val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "ImageMessageffhjhgvukhukghhjkvkjkkjvkhj", null)
            img_uri = Uri.parse(path)

            if (img_uri != null){
                add_ss_contact_us.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_plus_fill, 0, R.drawable.ic_check, 0)
            }


            if (img_uri == null) return
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/help-messages/$filename")
            ref.putFile(img_uri!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener{
                            img_url = it.toString()
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Not Uploaded${it.message}", Toast.LENGTH_SHORT).show()
                }

        }
    }
}