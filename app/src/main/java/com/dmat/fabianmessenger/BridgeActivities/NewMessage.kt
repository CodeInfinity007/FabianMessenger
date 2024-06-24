package com.dmat.fabianmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_messages.*

class NewMessage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        val recyclerview_newmsg: RecyclerView = findViewById(R.id.recyclerview_newmsg)

        val actionBar = supportActionBar
        actionBar?.elevation = 0f
        actionBar?.title = "New Conversation"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_home_indicator)

        val adapter = GroupAdapter<ViewHolder>()
        recyclerview_newmsg.adapter = adapter
//        recyclerview_newmsg.addItemDecoration(
//            DividerItemDecoration(
//                this,
//                DividerItemDecoration.VERTICAL
//            )
//        )

        fetchUsers()
    }

    private fun fetchUsers() {

        val recyclerview_newmsg: RecyclerView = findViewById(R.id.recyclerview_newmsg)
        val adapter = GroupAdapter<ViewHolder>()

        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {

                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }

                    adapter.setOnItemClickListener { item, view ->
                        val useritem = item as UserItem
                        val intent = Intent(view.context, chat_log::class.java)
                        intent.putExtra("user", useritem.user)
                        startActivity(intent)
                        finish()
                    }
                }
                recyclerview_newmsg.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}

class UserItem(val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.username_select_user).text = user.name
        Picasso.get().load(user.dp_url)
            .into(viewHolder.itemView.findViewById<ImageView>(R.id.default_dp))
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}

