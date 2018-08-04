package com.mobilelab.artyomska.bookdeposit.listAdapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mobilelab.artyomska.bookdeposit.MainActivity
import com.mobilelab.artyomska.bookdeposit.listHolder.UserHolder
import com.mobilelab.artyomska.bookdeposit.model.UserData
import com.mobilelab.artyomska.bookdeposit.utils.EndlessRecyclerViewScrollListener
import java.util.ArrayList

class UserAdapter(private val context: Context?, private val itemResource: Int, private val scrollListener: EndlessRecyclerViewScrollListener) : RecyclerView.Adapter<UserHolder>() {

    val userList: ArrayList<UserData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {

        val view = LayoutInflater.from(parent.context).inflate(this.itemResource, parent, false)
        return UserHolder(view, context, this)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {

        val book = this.userList[position]
        holder.bindUser(book)
    }

    override fun getItemCount(): Int {

        return this.userList.size
    }

    fun changeFragment(ID: String, ISBN: String, i: Int) {
        (context as MainActivity).changeFragment(ID, ISBN, i)

    }

    fun addNewDataPage(data: ArrayList<UserData>) {
        val size = userList.size + 1
        userList.addAll(data)
        notifyItemRangeInserted(size, data.size)
    }

}