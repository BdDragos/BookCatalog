package com.mobilelab.artyomska.bookdeposit.listAdapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.mobilelab.artyomska.bookdeposit.model.Review
import com.mobilelab.artyomska.bookdeposit.utils.EndlessRecyclerViewScrollListener
import com.mobilelab.artyomska.bookdeposit.listHolder.ReviewHolder

import java.util.ArrayList

class ReviewAdapter(private val context: Context, private val itemResource: Int, private val scrollListener: EndlessRecyclerViewScrollListener) : RecyclerView.Adapter<ReviewHolder>() {

    val reviewList: ArrayList<Review> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewHolder {

        val view = LayoutInflater.from(parent.context).inflate(this.itemResource, parent, false)
        return ReviewHolder(view, context, this)
    }

    override fun onBindViewHolder(holder: ReviewHolder, position: Int) {

        val review = this.reviewList[position]
        holder.bindReview(review)
    }

    override fun getItemCount(): Int {

        return this.reviewList.size
    }

    fun addNewDataPage(data: ArrayList<Review>) {
        val size = reviewList.size + 1
        reviewList.addAll(data)
        notifyItemRangeInserted(size, data.size)
    }

    fun clearList() {
        val size = reviewList.size + 1
        reviewList.clear()
        notifyDataSetChanged()
    }
}
