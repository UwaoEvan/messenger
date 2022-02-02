package com.evan.mymessenger.views

import com.evan.mymessenger.R
import com.evan.mymessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_info.view.*

class UserItem(val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_list.text = user.userName
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.profile_imageView)
    }

    override fun getLayout(): Int {
        return R.layout.user_info
    }
}