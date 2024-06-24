package com.dmat.fabianmessenger

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.dmat.fabianmessenger.Activities.Settings
import com.dmat.fabianmessenger.Models.LatestChatTime
import com.dmat.fabianmessenger.Models.UserItemForward
import com.github.pgreze.reactions.ReactionPopup
import com.github.pgreze.reactions.ReactionSelectedListener
import com.github.pgreze.reactions.dsl.reactionConfig
import com.github.pgreze.reactions.dsl.reactions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_chatfrom.view.*
import kotlinx.android.synthetic.main.activity_chatto.view.*
import kotlinx.android.synthetic.main.chat_log_actionbar.*
import kotlinx.android.synthetic.main.imgmsg_chatfrom.view.*
import kotlinx.android.synthetic.main.imgmsg_chatto.view.*
import kotlinx.android.synthetic.main.timedivider.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

var ToUid: String? = null
var FromUid: String? = null

class chat_log : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    var indicatorIndex = -1
    private var key: String? = null
    private var dividerrefkey: String? = null
    private var img_key: String? = null
    private var photoUri: Uri? = null
    private var bitmap: Bitmap? = null
    private var img_uri: Uri? = null
    private lateinit var imagefile: File
    private val Filename = "image"

    private var num: Any? = null

    var add_timedivider: Boolean = false

    val chat_msgMap = HashMap<String, ChatMessage>()
    val chat_posMap = HashMap<Int, String?>()
    var del_id: String? = null
    private var flag_del = false
    var pos: Int? = null
    var latest_chamsg_time: Date? = null

    var user: User? = null
    var selfuser: User? = null
    var indicator_timeout: Int = 11

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
//        recyclerview_chatlog.alpha = 1f
//        frame_chatlog.alpha = 0f
//        captureimg_chatlog.visibility = View.GONE

        user = intent.getParcelableExtra<User>("user")

        val selfuid = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$selfuid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                selfuser = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

//        recyclerview_chatlog.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))

        val actionBar = supportActionBar
        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar?.setCustomView(R.layout.chat_log_actionbar)
        Picasso.get().load(user?.dp_url).into(default_dp_chatto)
        name_chatlog_action.text = user?.name
//        actionBar?.title = "Messages"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_home_indicator)

        num = user?.phone_num

        val recyclerview_forward: RecyclerView = findViewById(R.id.recyclerview_chatlog)
        recyclerview_forward.adapter = adapter

        ToUid = FirebaseAuth.getInstance().uid
        FromUid = user?.uid

        val typref = FirebaseDatabase.getInstance().getReference("/users/$FromUid/isTyping")
        msg_box_chatlog.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val params = HashMap<String, Any>()
                params["from"] = p0!!.isNotEmpty()
                params["fromid"] = FromUid.toString()
                params["toUid"] = ToUid.toString()
                typref.updateChildren(params)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        val typref2 = FirebaseDatabase.getInstance().getReference("/users/$ToUid")
        typref2.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.child("to").value == true) {
                    // TODO: Update Typing Group to text (OPTIMISE)
                    val indicator =
                        ChatFromItem("indicator", "", -1, user!!, Calendar.getInstance().time)
                    indicatorIndex = adapter.itemCount
                    adapter.add(indicator)
                    recyclerview_chatlog.scrollToPosition(adapter.itemCount - 1)

//                    val timer = object: CountDownTimer(5000, 1000) {
//                        override fun onTick(millisUntilFinished: Long) {
//                            makeToast("tick")
//                        }
//
//                        override fun onFinish() {
//                            indicator_timeout = 112
//                            makeToast("5 Sec Ho gaye")
//                        }
//                    }
//                    timer.start()
//
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if (snapshot.child("from").value == false) {
                    if (indicatorIndex >= 0) {
                        adapter.removeGroup(indicatorIndex)
                    }

                }
                if (snapshot.child("from").value == true) {
                    indicatorIndex = adapter.itemCount
                    adapter.add(
                        ChatFromItem(
                            "indicator",
                            "",
                            -1,
                            user!!,
                            Calendar.getInstance().time
                        )
                    )
                    recyclerview_chatlog.scrollToPosition(adapter.itemCount - 1)
//
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })

        RecieveMessage()

        val config = reactionConfig(this) {
            reactions {
//                resId    { R.drawable.ic_heart_fill }
                resId { R.drawable.red_heart }
                resId { R.drawable.smile }
                resId { R.drawable.oh }
                resId { R.drawable.screaming }
                resId { R.drawable.angry }
                resId { R.drawable.thumbs_up }
                resId { R.drawable.ic_question }
            }
            popupCornerRadius = 100
            popupColor = Color.parseColor("#262629")
            popupAlpha = 255
            popupMargin = 100
            verticalMargin = horizontalMargin / 2
        }

        adapter.setOnItemLongClickListener { item, view ->
            pos = adapter.getAdapterPosition(item)
            Log.d("POSITIONKEY", pos.toString())
            val popup = PopupMenu(this, view)

            val rectionpopup = ReactionPopup(this, config, object : ReactionSelectedListener {
                override fun invoke(position: Int): Boolean = true.also {
                    makeToast("$position selected")
                    val ref = FirebaseDatabase.getInstance()
                        .getReference("/user-messages/$ToUid/$FromUid")
                    val sender_ref = FirebaseDatabase.getInstance()
                        .getReference("/user-messages/$FromUid/$ToUid")
                    refreshMessages(pos)

                    ref.child(del_id!!).child("reaction").setValue(position)
                    sender_ref.child(del_id!!).child("reaction").setValue(position)

                }
            })

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_copy -> {
                        makeToast("Message Copied to Clipboard")

                        val text = chat_msgMap[chat_posMap[pos]]!!.text

                        val sdk = Build.VERSION.SDK_INT
                        if (sdk < Build.VERSION_CODES.HONEYCOMB) {
                            val clipboard =
                                getSystemService(CLIPBOARD_SERVICE) as android.text.ClipboardManager
                            clipboard.text = text
                        } else {
                            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Message", text)
                            clipboard.setPrimaryClip(clip)
                        }

//                        makeToast(chat_msgMap[chat_posMap[pos]]!!.text)
                        true
                    }
                    R.id.menu_delete -> {
                        showdeldialog()
                        true
                    }

                    R.id.menu_forward -> {
                        show_forward_dialog()
                        true
                    }

                    R.id.menu_reply -> {
                        makeToast("Reply")
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
            popup.setOnDismissListener {
//                chat_log_page.alpha = 1f
//                recyclerview_chatlog.alpha = 1f
//                frame_chatlog.alpha = 0f
//                rectionpopup.dismiss()
            }

            popup.inflate(R.menu.msg_menu)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                popup.setForceShowIcon(true)
            }
            popup.show()
//            chat_log_page.alpha = 0.5f
//            recyclerview_chatlog.alpha = 0.4f
//            frame_chatlog.alpha = 0.1f


            view.setOnTouchListener(rectionpopup)
            true
        }

        send_button_chatlog.setOnClickListener { SendMessage() }

        select_img_chatlog.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)

            //RecieveImgMessage()
        }

        captureimg_chatlog.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            imagefile = getimagefile(Filename)
            val file_provider = FileProvider.getUriForFile(
                this,
                "com.dmat.fabianmessenger1.fileprovider",
                imagefile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file_provider)
            startActivityForResult(takePictureIntent, 1)
        }

        call_chatlog.setOnClickListener {
            val dialintent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${num.toString()}"))
            startActivity(dialintent)
        }

        videocall_chatlog.setOnClickListener {
//            val intent = Intent(Intent)
        }

        info_chatlog.setOnClickListener { show_contact_info_dialog() }

    }

    private fun getimagefile(filename: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(filename, ".jpg", storageDirectory)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 0 && data != null) {
            val compressIMG = selfuser?.compressImg
            photoUri = data.data
            val bytes = ByteArrayOutputStream()
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            if (compressIMG!!) {
                makeToast("Compressing");bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, bytes)
            }
            val path = MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap,
                "ImageMessageffhjhjkvkjkkjvkhj",
                null
            )
            img_uri = Uri.parse(path)


//            show_img_msg_dialog(img_uri!!)
//            show_img_msg_dialog()

        }

        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            val bytes = ByteArrayOutputStream()
            bitmap = BitmapFactory.decodeFile(imagefile.absolutePath)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
            val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Image", null)
            img_uri = Uri.parse(path)

        }
    }

    private fun SendMessage() {
        val text = msg_box_chatlog.text.toString()
        val ToUid = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>("user")
        val FromUid = user?.uid

        val timedividerref =
            FirebaseDatabase.getInstance().getReference("/user-messages/$ToUid/$FromUid").push()
        dividerrefkey = timedividerref.key

        val ref =
            FirebaseDatabase.getInstance().getReference("/user-messages/$ToUid/$FromUid").push()
        key = ref.key
        val img_ref =
            FirebaseDatabase.getInstance().getReference("/user-messages/$ToUid/$FromUid").push()



        val sender_img_ref =
            FirebaseDatabase.getInstance().getReference("/user-messages/$FromUid/$ToUid/").push()
        val sender_ref =
            FirebaseDatabase.getInstance().getReference("/user-messages/$FromUid/$ToUid/$key")
        val latestmsg_ref =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$ToUid/$FromUid")
        val sender_latestmsg_ref =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$FromUid/$ToUid")

        val latesttime_ref =
            FirebaseDatabase.getInstance().getReference("/latest-time/$ToUid/$FromUid")

        if (ToUid == null) return
        if (FromUid == null) return

        val chatmessage =
            ChatMessage(key!!, -1, text, ToUid, FromUid, Calendar.getInstance().time)

        latesttime_ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                latest_chamsg_time = snapshot.getValue(LatestChatTime::class.java)?.timeStampdivider
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        latesttime_ref.child("timeStampdivider").setValue(chatmessage.timeStamp)

        if (latest_chamsg_time != null) {
            placetimedivider(Calendar.getInstance().time, latest_chamsg_time!!)
            if (add_timedivider) {
                val timedividermsg = LatestChatTime(dividerrefkey!!, chatmessage.timeStamp, true, ToUid, FromUid)
                timedividerref.setValue(timedividermsg) ///////////////////////////////////////////////////////////////////////////////////////
                    .addOnSuccessListener { Log.d("DBGPOSITION", "TimeDivider Placed") }
                add_timedivider = false
            }
        }

        if (text != "") {
            ref.setValue(chatmessage)
                .addOnSuccessListener {
//                    recyclerview_chatlog.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
                    Log.d("DBGPOSITION", "Chat Placed")
                    msg_box_chatlog.text.clear()
                    recyclerview_chatlog.scrollToPosition(adapter.itemCount - 1)
                }
        }


        sender_ref.setValue(chatmessage)
        latestmsg_ref.setValue(chatmessage)
        sender_latestmsg_ref.setValue(chatmessage)

//        val uri = intent.getStringExtra("uri")
//        img_uri = Uri.parse(uri)

        if (img_uri == null) return
//        val extra = intent.getBooleanExtra("flag", false)
//        if (!extra) return
        val filename = UUID.randomUUID().toString()
        val refimg_up = FirebaseStorage.getInstance().getReference("/image_msg/$filename")

        refimg_up.putFile(img_uri!!)
            .addOnSuccessListener {
                refimg_up.downloadUrl.addOnSuccessListener {
                    val img_url = it.toString()
                    img_key = img_ref.key

                    val imgMessage =
                        ImageMessage(
                            img_key!!, img_url, ToUid, FromUid, Calendar.getInstance().time
                        )

                    img_ref.setValue(imgMessage)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show()
                            recyclerview_chatlog.scrollToPosition(adapter.itemCount - 1)
                        }
                    sender_img_ref.setValue(imgMessage)
                    photoUri = null
                    img_uri = null

                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Not Uploaded${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun RecieveMessage() {
        val ToUid = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>("user")
        val FromUid = user?.uid

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$ToUid/$FromUid")
        //val sender_ref = FirebaseDatabase.getInstance().getReference("/user-messages/$FromUid/$ToUid")
        //val img_ref = FirebaseDatabase.getInstance().getReference("/image-messages/$ToUid/$FromUid")

//        var change: Int = 0
//        ref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                change += 1
//                Log.d("nvowrvowivnwipv", "Data Changed")
//                Log.d("nvowrvowivnwipv", change.toString())
//
//
//                if (change != 1) {
//                    finish()
//                    makeToast("yo")
//                    overridePendingTransition(0, 0)
//                    startActivity(intent)
//                    overridePendingTransition(0, 0)
//                    val reaction_ref =
//                        FirebaseDatabase.getInstance().getReference("/chatroom/$ToUid/$FromUid/reacted")
//                    val reaction_ref_sender =
//                        FirebaseDatabase.getInstance().getReference("/chatroom/$FromUid/$ToUid/reacted")
//                    reaction_ref.setValue(false)
//                    reaction_ref_sender.setValue(false)
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {}
//        })


        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val timedividermsg = snapshot.getValue(LatestChatTime::class.java)
                val chatmsg = snapshot.getValue(ChatMessage::class.java)
                val img_msg = snapshot.getValue(ImageMessage::class.java)

                if (timedividermsg != null) {
                    if (timedividermsg.toid == FirebaseAuth.getInstance().uid) {
                        if (timedividermsg.flag) {
                            adapter.add(TimeDivider(timedividermsg.timeStampdivider))
//                            chat_msgMap[chatmsg.id] = chatmsg
                            chat_posMap[adapter.itemCount - 1] = timedividermsg.id
                            refreshMessages()
                        }
                    }
                }

                if (chatmsg != null) {
                    if (chatmsg.toUid == FirebaseAuth.getInstance().uid) {
                        if (chatmsg.text != "") {
                            adapter.add(
                                ChattoItem(
                                    chatmsg.text,
                                    chatmsg.reaction,
                                    chatmsg.timeStamp
                                )
                            )
                            chat_msgMap[chatmsg.id] = chatmsg
                            chat_posMap[adapter.itemCount - 1] = chatmsg.id
                            refreshMessages()
                        }

                        if (img_msg!!.img_url != "") {
                            adapter.add(ChatImageToItem(img_msg.img_url, img_msg.timeStamp))
                            chat_msgMap[chatmsg.id] = chatmsg
                            chat_posMap[adapter.itemCount - 1] = chatmsg.id
                            refreshMessages()
                        }
                    } else {
                        val fromuser = intent.getParcelableExtra<User>("user")

                        if (chatmsg.text != "") {
                            adapter.add(
                                ChatFromItem(
                                    "chat",
                                    chatmsg.text,
                                    chatmsg.reaction,
                                    fromuser!!,
                                    chatmsg.timeStamp
                                )
                            )
                            chat_msgMap[chatmsg.id] = chatmsg
                            chat_posMap[adapter.itemCount - 1] = chatmsg.id
                            refreshMessages()
                        }

                        if (img_msg!!.img_url != "") {
                            adapter.add(ChatImageFromItem(img_msg.img_url, img_msg.timeStamp))
                            chat_msgMap[chatmsg.id] = chatmsg
                            chat_posMap[adapter.itemCount - 1] = chatmsg.id
                            refreshMessages()
                        }

                    }
                }
                recyclerview_chatlog.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(error: DatabaseError) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                adapter.clear()
                retartAct()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }

    private fun makeToast(text: String) {
        return Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun placetimedivider(timestamp: Date, lattime: Date) {
        val min = SimpleDateFormat("ddHHmm")

        val current_min = min.format(timestamp).toInt()
        val latest_min = min.format(lattime).toInt()

        val diff_min = current_min - latest_min

        if (diff_min >= 15) {
            Log.d("DATETIME", diff_min.toString())
            add_timedivider = true
        }
    }

    private fun refreshMessages(pos: Int? = null, refresh: Int? = null) {
        if (pos != null) {
            del_id = chat_posMap.values.toList()[pos]
            Log.d("MAPIINDEXTEXT", chat_posMap.toString())
            Log.d("MAPIINDEXTEXT", chat_msgMap.toString())
        }
    }

    private fun showdeldialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_dialog_layout)

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        val del_dialog = dialog.findViewById<Button>(R.id.del_dialog)
        val cancel_dialog = dialog.findViewById<Button>(R.id.cancel_dialog)

        del_dialog.setOnClickListener {
            dialog.dismiss()
            flag_del = true
            adapter.removeGroup(pos!!)
            val dbref = FirebaseDatabase.getInstance()
                .getReference("/user-messages/$ToUid/$FromUid")
            val dbref_sender = FirebaseDatabase.getInstance()
                .getReference("/user-messages/$FromUid/$ToUid")


            dbref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    refreshMessages(pos)
                    snapshot.child(del_id!!).ref.removeValue()
                        .addOnSuccessListener {
                            chat_posMap.remove(pos)
                            chat_msgMap.remove(del_id)
                            makeToast("Delete")
                        }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
            dbref_sender.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    refreshMessages(pos)
                    snapshot.child(del_id!!).ref.removeValue()
                        .addOnSuccessListener {
                            chat_posMap.remove(pos)
                            chat_msgMap.remove(del_id)
                            makeToast("Delete kardiya")
                        }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        cancel_dialog.setOnClickListener { dialog.dismiss() }

    }

    private fun show_contact_info_dialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.contact_info_popup_layout)

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        val dp_contact_info = dialog.findViewById<CircleImageView>(R.id.dp_contact_info)
        val name_contact_info = dialog.findViewById<TextView>(R.id.name_contact_info)
        val phone_contact_info = dialog.findViewById<TextView>(R.id.phone_contact_info)
        val msg_contact_info = dialog.findViewById<Button>(R.id.msg_contact_info)
        val call_contact_info = dialog.findViewById<Button>(R.id.call_contact_info)
        val video_contact_info = dialog.findViewById<Button>(R.id.video_contact_info)
        val share_button_contact_info = dialog.findViewById<Button>(R.id.share_button_contact_info)
        val block_button_contact_info = dialog.findViewById<Button>(R.id.block_button_contact_info)
        val edit_button_contact_info = dialog.findViewById<TextView>(R.id.edit_button_contact_info)
        val backbutton_contact_info = dialog.findViewById<TextView>(R.id.backbutton_contact_info)

        Picasso.get().load(user?.dp_url).into(dp_contact_info)
        name_contact_info.text = user?.name
        phone_contact_info.text = "   ${user?.phone_num}"

        call_contact_info.setOnClickListener {
            dialog.dismiss()
            val dialintent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${num.toString()}"))
            startActivity(dialintent)
        }

        msg_contact_info.setOnClickListener { dialog.dismiss() }
        backbutton_contact_info.setOnClickListener { dialog.dismiss() }
        video_contact_info.setOnClickListener { makeToast("Feature Coming Soon...") }

        phone_contact_info.setOnClickListener {
            val sdk = Build.VERSION.SDK_INT
            if (sdk < Build.VERSION_CODES.HONEYCOMB) {
                val clipboard =
                    getSystemService(CLIPBOARD_SERVICE) as android.text.ClipboardManager
                clipboard.text = phone_contact_info.text
                makeToast("Number Copied to Clipboard")
            } else {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Message", phone_contact_info.text)
                clipboard.setPrimaryClip(clip)
                makeToast("Number Copied to Clipboard")
            }
        }
        edit_button_contact_info.setOnClickListener {
            val intent = Intent(this@chat_log, Settings::class.java)
            startActivity(intent)
        }
        share_button_contact_info.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Name: ${user?.name} \nNumber: ${phone_contact_info.text}"
            )
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To:"))
            dialog.dismiss()
        }
        block_button_contact_info.setOnClickListener { makeToast("Feature Coming Soon...") }

    }

    private fun show_forward_dialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.forward_popup)

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        val recyclerview_forward: RecyclerView = dialog.findViewById(R.id.recyclerview_forward)

//        val recyclerview_forward: RecyclerView = findViewById(R.id.recyclerview_forward)
        val adapterf = GroupAdapter<ViewHolder>()

        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {

                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        Log.d("nnwfwfhowe", user.name)
                        adapterf.add(UserItemForward(user))

                    }

//                    adapter.setOnItemClickListener { item, view ->
//                        val useritem = item as UserItem
//                        val intent = Intent(view.context, chat_log::class.java)
//                        intent.putExtra("user", useritem.user)
//                        startActivity(intent)
//                        finish()
//                    }
                }
                recyclerview_forward.adapter = adapterf
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }

    private fun show_img_msg_dialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottom_image_dialog)

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

//        val send_button_send_image = dialog.findViewById<TextView>(R.id.send_button_send_image)
//        val image_send_image = dialog.findViewById<ImageView>(R.id.image_send_image)
//
//        Picasso.get().load(imguri).into(image_send_image)
//
//        send_button_send_image.setOnClickListener { dialog.dismiss() }


    }

    private fun retartAct() {
//        finish()
//        makeToast("Restarted")
        overridePendingTransition(0, 0)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    class ChattoItem(val text: String, private val reaction_num: Int, private val timestamp: Date) :
        Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            val reactions = listOf(
                R.drawable.red_heart,
                R.drawable.smile,
                R.drawable.oh,
                R.drawable.screaming,
                R.drawable.angry,
                R.drawable.thumbs_up,
                R.drawable.ic_question
            )

            setdate(viewHolder)
//            Toast.makeText(viewHolder.itemView.context, "Hello Bird", Toast.LENGTH_SHORT).show()
            viewHolder.itemView.reaction_chatto.visibility = View.VISIBLE
            if (reaction_num != -1) {
                viewHolder.itemView.reaction_chatto.setImageResource(reactions[reaction_num])
                viewHolder.itemView.reaction_chatto.startAnimation(
                    AnimationUtils.loadAnimation(
                        viewHolder.itemView.context,
                        R.anim.zoom_in
                    )
                )

            }
            viewHolder.itemView.textto.text = text
            viewHolder.itemView.chatto.startAnimation(
                AnimationUtils.loadAnimation(
                    viewHolder.itemView.context,
                    R.anim.fade_in
                )
            )
        }

        override fun getLayout(): Int {
            return R.layout.activity_chatto
        }

        private fun setdate(viewHolder: ViewHolder) {
            val dateformat =
                SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
//            viewHolder.itemView.timestamp_chatto.text = dateformat.format(timestamp)
        }
    }

    class ChatFromItem(
        private val flag: String,
        val text: String,
        private val reaction_num: Int,
        private val user: User,
        private val timestamp: Date
    ) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            if (flag == "chat") {
                val reactions = listOf(
                    R.drawable.red_heart,
                    R.drawable.smile,
                    R.drawable.oh,
                    R.drawable.screaming,
                    R.drawable.angry,
                    R.drawable.thumbs_up,
                    R.drawable.ic_question
                )
                viewHolder.itemView.reaction_chatfrom.visibility = View.VISIBLE
                if (reaction_num != -1) {
                    viewHolder.itemView.reaction_chatfrom.setImageResource(reactions[reaction_num])
                }

                viewHolder.itemView.indicatorView_chatfrom.visibility = View.GONE
                viewHolder.itemView.textfrom.text = text
                Picasso.get().load(user.dp_url).into(viewHolder.itemView.default_dp_chatto_chatlog)
                setdate(viewHolder)
            }

            if (flag == "indicator") {
                viewHolder.itemView.textfrom.visibility = View.GONE
//                viewHolder.itemView.default_dp_chatto_chatlog.visibility = View.GONE
                val params: ViewGroup.MarginLayoutParams =
                    viewHolder.itemView.indicatorView_chatfrom.layoutParams as ViewGroup.MarginLayoutParams
                params.topMargin = 40
//                params.marginStart = 40
//                params.leftMargin = 40
                Picasso.get().load(user.dp_url).into(viewHolder.itemView.default_dp_chatto_chatlog)
            }

        }

        override fun getLayout(): Int {
            return R.layout.activity_chatfrom
        }

        private fun setdate(viewHolder: ViewHolder) {
            val dateformat =
                SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
//            viewHolder.itemView.timestamp_chatfrom.text = dateformat.format(timestamp)
        }
    }

    class ChatImageToItem(private val img_url: String, private val timestamp: Date) :
        Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            val textto_img = viewHolder.itemView.textto_img
            val path = img_url
            Picasso.get().load(path).into(textto_img)

            setdate(viewHolder)
        }

        override fun getLayout(): Int {
            return R.layout.imgmsg_chatto
        }

        private fun setdate(viewHolder: ViewHolder) {
            val dateformat =
                SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
            viewHolder.itemView.timestamp_chatto_img.text = dateformat.format(timestamp)
        }
    }

    class ChatImageFromItem(private val img_url: String, private val timestamp: Date) :
        Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            val textfrom_img = viewHolder.itemView.textfrom_img
            val path = img_url
            Picasso.get().load(path).into(textfrom_img)
            setdate(viewHolder)
        }

        override fun getLayout(): Int {
            return R.layout.imgmsg_chatfrom
        }

        private fun setdate(viewHolder: ViewHolder) {
            val dateformat =
                SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
            viewHolder.itemView.timestamp_chatfrom_img.text = dateformat.format(timestamp)
        }
    }

    class TimeDivider(private val timestamp: Date) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            setdate(viewHolder)
        }

        override fun getLayout(): Int {
            return R.layout.timedivider
        }

        private fun setdate(viewHolder: ViewHolder) {
            val dateformat =
                SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)

            viewHolder.itemView.divider_time.text = dateformat.format(timestamp)
        }
    }


}



