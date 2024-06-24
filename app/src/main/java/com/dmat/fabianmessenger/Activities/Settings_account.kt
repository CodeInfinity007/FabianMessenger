package com.dmat.fabianmessenger.Activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dmat.fabianmessenger.MainActivity
import com.dmat.fabianmessenger.Models.HelpCentreModel
import com.dmat.fabianmessenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_settings_account.*
import kotlinx.android.synthetic.main.activity_settings_contact_us.*
import kotlinx.android.synthetic.main.email_change_popup.*

class Settings_account : AppCompatActivity() {
    var email: String = "null"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_account)

        val actionBar = supportActionBar
        actionBar?.elevation = 0f
        actionBar?.title = "Account"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_home_indicator)

        val ToUid = FirebaseAuth.getInstance().uid

        email_change_acc_settings.setOnClickListener {
            showemail_changedialog()
            val ref = FirebaseDatabase.getInstance().getReference("Help-Center/$ToUid").push()
            val helpmsg = HelpCentreModel(ref.key!!, "Requesting Email Change", "", email, ToUid.toString())
            ref.setValue(helpmsg)

        }

        del_acc_settings.setOnClickListener {
            showsignout_dialog()
        }

    }

    private fun showemail_changedialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.email_change_popup)

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        val ok_button = dialog.findViewById<Button>(R.id.ok_acc_change_req)
        ok_button.setOnClickListener {
            email = email_acc_change_req.text.toString()
            Toast.makeText(this@Settings_account, "Successfully Requested", Toast.LENGTH_LONG).show()
            dialog.dismiss()
            finish()

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
        val desciption_signout = dialog.findViewById<TextView>(R.id.desciption_signout)
        signout.text = "Delete"
        desciption_signout.text = "This Process can't be undone. Are you sure?"

        val curruser = FirebaseAuth.getInstance().currentUser

        cancel.setOnClickListener { dialog.dismiss() }
        signout.setOnClickListener {
            dialog.dismiss()

            FirebaseAuth.getInstance().signOut()
            curruser!!.delete()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }
    }
}