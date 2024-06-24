package com.dmat.fabianmessenger.Activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dmat.fabianmessenger.R
import com.dmat.fabianmessenger.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream
import java.util.*

class Profile : AppCompatActivity() {
    var selfuser: User? = null
    var photoUri: Uri? = null
    var profileURL: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val actionBar = supportActionBar
        actionBar?.elevation = 0f
        actionBar?.title = "Profile"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_home_indicator)

//        var selfuser: User? = null
//
//        val selfuid = FirebaseAuth.getInstance().currentUser?.uid
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$selfuid")
//        ref.addListenerForSingleValueEvent(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                selfuser = snapshot.getValue(User::class.java)
//                Toast.makeText(this@Profile, selfuser?.name, Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onCancelled(error: DatabaseError) {}
//        })


        val selfuser = intent.getParcelableExtra<User>("selfuser")

        name_profile.text = selfuser?.name
        phone_profile.text = selfuser?.phone_num
        Picasso.get().load(selfuser?.dp_url).into(dp_profile)

        name_edit_profile_button.setOnClickListener { shownamedialog() }
        about_edit_profile_button.setOnClickListener { showaboutdialog() }
        phone_edit_profile_button.setOnClickListener { showphonedialog() }
        dp_profile.setOnClickListener { select_dp() }

    }

    private fun select_dp() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    private fun shownamedialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_name_edit_profile_layout)

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        val name_profile_edit = dialog.findViewById<EditText>(R.id.name_profile_edit)
        val cancel_name_edit_profile = dialog.findViewById<Button>(R.id.cancel_name_edit_profile)
        val ok_name_edit_profile = dialog.findViewById<Button>(R.id.ok_name_edit_profile)

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        name_profile_edit.hint = selfuser?.name
        cancel_name_edit_profile.setOnClickListener { dialog.dismiss() }
        ok_name_edit_profile.setOnClickListener {
            dialog.dismiss()
            if (name_profile_edit.text != null) {
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var user = snapshot.getValue(User::class.java)
                        user!!.name = name_profile_edit.text.toString()
                        ref.setValue(user)
                            .addOnSuccessListener {
                                name_profile.text = name_profile_edit.text.toString()
                            }


                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }

    }

    private fun showaboutdialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_about_edit_profile_layout)

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        val about_profile_edit = dialog.findViewById<EditText>(R.id.about_profile_edit)
        val cancel_name_edit_profile = dialog.findViewById<Button>(R.id.cancel_name_edit_profile)
        val ok_name_edit_profile = dialog.findViewById<Button>(R.id.ok_name_edit_profile)

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

//        about_profile_edit.hint = selfuser?.name
        cancel_name_edit_profile.setOnClickListener { dialog.dismiss() }
        ok_name_edit_profile.setOnClickListener {
            dialog.dismiss()
//            if (about_profile_edit.text != null){
//                ref.addListenerForSingleValueEvent(object: ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        var user = snapshot.getValue(User::class.java)
//                        user!!.name = about_profile_edit.text.toString()
//                        ref.setValue(user)
//                            .addOnSuccessListener {
//                                name_profile.text = about_profile_edit.text.toString()
//                            }
//
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {}
//                })
//            }
        }

    }

    private fun showphonedialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_phone_edit_profile_layout)

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        val phone_profile_edit = dialog.findViewById<EditText>(R.id.phone_profile_edit)
        val cancel_name_edit_profile = dialog.findViewById<Button>(R.id.cancel_name_edit_profile)
        val ok_name_edit_profile = dialog.findViewById<Button>(R.id.ok_name_edit_profile)

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        phone_profile_edit.hint = selfuser?.phone_num
        cancel_name_edit_profile.setOnClickListener { dialog.dismiss() }
        ok_name_edit_profile.setOnClickListener {
            dialog.dismiss()
            if (phone_profile_edit.text != null) {
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var user = snapshot.getValue(User::class.java)
                        user!!.phone_num = phone_profile_edit.text.toString()
                        ref.setValue(user)
                            .addOnSuccessListener {
                                phone_profile.text = phone_profile_edit.text.toString()
                            }

                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 0 && data != null) {

            // Setting Pic on ImageView
            val photouri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photouri)
            dp_profile.setImageBitmap(bitmap)

            // Compressing & Getting URI
            val bytes = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, bytes)
            val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Image", null)
            photoUri = Uri.parse(path)

            val filename = UUID.randomUUID().toString()
            val ref1 = FirebaseStorage.getInstance().getReference("/images/$filename")
            ref1.putFile(photoUri!!)
                .addOnSuccessListener {
                    ref1.downloadUrl.addOnSuccessListener {
                        profileURL = it.toString()
                        val uid = FirebaseAuth.getInstance().uid
                        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
                        ref.addListenerForSingleValueEvent(object : ValueEventListener {

                            override fun onDataChange(snapshot: DataSnapshot) {
                                var user = snapshot.getValue(User::class.java)
                                if (profileURL != null) {
                                    user!!.dp_url = profileURL!!
                                }

                                ref.setValue(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this@Profile,
                                            "Success",
                                            Toast.LENGTH_SHORT
                                        )
                                    }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }
        }
    }
}