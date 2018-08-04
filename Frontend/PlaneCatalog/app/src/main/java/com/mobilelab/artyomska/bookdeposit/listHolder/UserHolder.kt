package com.mobilelab.artyomska.bookdeposit.listHolder

import android.content.Context
import android.graphics.BitmapFactory
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mobilelab.artyomska.bookdeposit.R
import com.mobilelab.artyomska.bookdeposit.listAdapter.UserAdapter
import com.mobilelab.artyomska.bookdeposit.model.UserData


class UserHolder(itemView: View, val context: Context?, private val mAdapter: UserAdapter) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var thumbNail: ImageView = itemView.findViewById(R.id.userProfilePic) as ImageView
    var nameOfUser: TextView = itemView.findViewById(R.id.userNameDetail) as TextView
    var noOfFriends: TextView = itemView.findViewById(R.id.noOfFriendsDetail) as TextView
    var layout: ConstraintLayout = itemView.findViewById(R.id.layoutForUserElem) as ConstraintLayout

    var user: UserData? = null


    fun bindUser(user: UserData) {

        layout.setOnClickListener(this)
        val x = Base64.decode(user.userPic, Base64.DEFAULT)
        val bmp = BitmapFactory.decodeByteArray(x, 0, x.size)

        this.user = user
        this.thumbNail.setImageBitmap(bmp);
        this.nameOfUser.text = user.username
        this.noOfFriends.text = user.noOfFriends

    }

    override fun onClick(v: View) {


        val userD = this.user
        when (v.id) {
            R.id.layoutForUserElem -> {
                mAdapter.changeFragment(Integer.toString(userD!!.id), Integer.toString(0), 3)
            }


        }
    }
}