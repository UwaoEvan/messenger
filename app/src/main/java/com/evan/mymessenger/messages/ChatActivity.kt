package com.evan.mymessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.evan.mymessenger.R
import com.evan.mymessenger.models.ChatMessage
import com.evan.mymessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_from.view.*
import kotlinx.android.synthetic.main.chat_to.view.*

class ChatActivity : AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user?.userName

        chats_Flow.adapter = adapter

        listenForMessages()
        send_button.setOnClickListener {
            sendMessage()
        }
    }

    private fun listenForMessages(){

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        if(fromId.isNullOrEmpty() || toId.isNullOrEmpty()) return
        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if(chatMessage != null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = DashboardActivity.currentUser
                        adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, user))
                    }
                } else return
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
//                TODO("Not yet implemented")
            }
        })
    }

    private fun sendMessage() {

        val text = chat_input.text.toString()
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid
        val fromId = FirebaseAuth.getInstance().uid
        if (toId == null || fromId == null) return
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

            val chatMessage = ChatMessage(ref.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
            ref.setValue(chatMessage)
                .addOnSuccessListener {
                    chats_Flow.scrollToPosition(adapter.itemCount -1)
                    chat_input.text.clear()
                }
            toRef.setValue(chatMessage)
        val latestFromRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
            latestFromRef.setValue(chatMessage)
        val latestToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
                latestToRef.setValue(chatMessage)
        }
}

class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chatTo_text.text = text
        val imageHolder = viewHolder.itemView.chatTo_imageView
        Picasso.get().load(user.profileImageUrl).into(imageHolder)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to
    }
}

class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chatFrom_text.text = text
        val imageHolder = viewHolder.itemView.chatFrom_imageView
        Picasso.get().load(user.profileImageUrl).into(imageHolder)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from
    }
}