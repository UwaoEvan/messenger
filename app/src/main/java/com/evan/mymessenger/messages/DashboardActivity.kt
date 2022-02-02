package com.evan.mymessenger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.evan.mymessenger.registration.LoginActivity
import com.evan.mymessenger.registration.MainActivity
import com.evan.mymessenger.R
import com.evan.mymessenger.models.ChatMessage
import com.evan.mymessenger.models.User
import com.evan.mymessenger.views.ChatsItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.user_info.view.*
import kotlinx.android.synthetic.main.users_texts.view.*

class DashboardActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
    }
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        chats_recycler.adapter = adapter
        adapter.setOnItemClickListener { item, view ->

            val row = item as ChatsItem
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartner)
            startActivity(intent)
        }

        chats_recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        latestMessages()
        checkIfLoggedIn()
        fetchCurrentUser()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.new_message -> {
                var intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.signout -> {
                FirebaseAuth.getInstance().signOut()
                var intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshLatestMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(ChatsItem(it))
        }
    }

    private fun latestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                latestMessagesMap[snapshot.key!!] = chatMessage!!
                refreshLatestMessages()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                latestMessagesMap[snapshot.key!!] = chatMessage!!
                refreshLatestMessages()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }


            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference(("/users/$uid"))
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun checkIfLoggedIn (){
        var uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            var intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK. or (Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}