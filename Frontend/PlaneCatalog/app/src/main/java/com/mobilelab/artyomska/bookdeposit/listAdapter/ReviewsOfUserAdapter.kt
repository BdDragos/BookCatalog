package com.mobilelab.artyomska.bookdeposit.listAdapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mobilelab.artyomska.bookdeposit.MainActivity
import com.mobilelab.artyomska.bookdeposit.R
import com.mobilelab.artyomska.bookdeposit.listHolder.*
import com.mobilelab.artyomska.bookdeposit.model.Review
import java.util.ArrayList

class ReviewsOfUserAdapter(private val context: Context?) : RecyclerView.Adapter<HolderAbstractClass>() {

    private var reviewList: ArrayList<Review> = ArrayList()

    override fun getItemViewType(position: Int): Int {

        return position % 2 * 2
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): HolderAbstractClass {
        return when (viewType) {
            0 -> {
                val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.book_item_layout, viewGroup, false)
                BookHolderUserReview(view, context, this)
            }
            2 -> {
                val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.review_item_layout, viewGroup, false)
                ReviewHolderUserReview(view, context, this)
            }
            else -> {
                val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.book_item_layout, viewGroup, false)
                BookHolderUserReview(view, context, this)

            }
        }
    }

    override fun onBindViewHolder(viewHolder: HolderAbstractClass, position: Int) {

        when (viewHolder.itemViewType) {
            0 -> {
                val book = this.reviewList[position]
                viewHolder.bindType(book)
            }
            2 -> {
                val rev = this.reviewList[position]
                viewHolder.bindType(rev)
            }
        }
    }


    override fun getItemCount(): Int {

        return this.reviewList.size
    }

    fun changeFragment(ID: String, ISBN: String, i: Int) {
        (context as MainActivity).changeFragment(ID, ISBN, i)
    }

    fun addNewDataPage(data: ArrayList<Review>) {
        val size = reviewList.size + 1
        reviewList.addAll(data)
        notifyItemRangeInserted(size, data.size)
    }

}