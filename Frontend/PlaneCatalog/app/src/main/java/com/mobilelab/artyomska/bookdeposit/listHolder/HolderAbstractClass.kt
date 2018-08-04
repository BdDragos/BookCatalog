package com.mobilelab.artyomska.bookdeposit.listHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.mobilelab.artyomska.bookdeposit.model.Review


abstract class HolderAbstractClass(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bindType(item: Review)

}