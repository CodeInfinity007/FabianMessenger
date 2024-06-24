package com.dmat.fabianmessenger

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_msg_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class LatestMessageRow(val chatMessage: ChatMessage) :
    Item<ViewHolder>() {
    var chatPartnerUser: User? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.text_lat_user.text = chatMessage.text
        val dateformat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
        viewHolder.itemView.timestamp_lat_user.text = "${dateformat.format(chatMessage.timeStamp)}  "

        var chatPartnerId = ""
        if (chatMessage.toUid == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.fromUid
        } else {
            chatPartnerId = chatMessage.toUid
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)

                viewHolder.itemView.username_lat_user.text = chatPartnerUser?.name
                Picasso.get().load(chatPartnerUser?.dp_url)
                    .into(viewHolder.itemView.default_dp_lat_msg)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }

    override fun getLayout(): Int {
        return R.layout.latest_msg_row
    }

}



