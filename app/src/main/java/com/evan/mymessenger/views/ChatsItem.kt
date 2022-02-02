package com.evan.mymessenger.views

import com.evan.mymessenger.R
import com.evan.mymessenger.models.ChatMessage
import com.evan.mymessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.users_texts.view.*

class ChatsItem(val chatMessage: ChatMessage): Item<GroupieViewHolder>(){
    var chatPartner: User? = null
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.user_text.text = chatMessage.text
        val chatPartnerId: String
        if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartner = snapshot.getValue(User::class.java)
                viewHolder.itemView.username_usersList.text = chatPartner?.userName
                val imageHolder = viewHolder.itemView.profile_ChatImage
                Picasso.get().load(chatPartner?.profileImageUrl).into(imageHolder)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.users_texts
    }
}