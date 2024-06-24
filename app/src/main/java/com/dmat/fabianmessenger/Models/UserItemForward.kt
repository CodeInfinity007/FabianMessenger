package com.dmat.fabianmessenger.Models

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.dmat.fabianmessenger.R
import com.dmat.fabianmessenger.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class UserItemForward(val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.username_forward).text = user.name
        Log.d("nnwfwfhowe", user.uid)
        Toast.makeText(viewHolder.itemView.context, "Here", Toast.LENGTH_SHORT).show()
        Picasso.get().load(user.dp_url)
            .into(viewHolder.itemView.findViewById<ImageView>(R.id.default_dp_forward))
    }

    override fun getLayout(): Int {
        return R.layout.forward_users
    }
}