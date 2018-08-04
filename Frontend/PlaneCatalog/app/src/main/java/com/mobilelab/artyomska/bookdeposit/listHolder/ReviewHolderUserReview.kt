package com.mobilelab.artyomska.bookdeposit.listHolder

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.mobilelab.artyomska.bookdeposit.R
import com.mobilelab.artyomska.bookdeposit.listAdapter.ReviewsOfUserAdapter
import com.mobilelab.artyomska.bookdeposit.model.Review

class ReviewHolderUserReview(itemView: View, val context: Context?, private val mAdapter: ReviewsOfUserAdapter) : HolderAbstractClass(itemView), View.OnClickListener {

    private val userImage: ImageView = itemView.findViewById(R.id.userImageRew)
    private val usernameRewId: TextView = itemView.findViewById(R.id.usernameRewId)
    private val ratingRewId: TextView = itemView.findViewById(R.id.ratingRewId)
    private val reviewTextId: TextView = itemView.findViewById(R.id.reviewTextId)
    private val ratingBarId: RatingBar = itemView.findViewById(R.id.ratingBarId)
    private val reviewDateId: TextView = itemView.findViewById(R.id.reviewDateId)

    private var review: Review? = null

    @SuppressLint("SetTextI18n")
    override fun bindType(review: Review) {

        this.review = review
        val x = Base64.decode(review.user.userPic, Base64.DEFAULT)
        val bmp = BitmapFactory.decodeByteArray(x, 0, x.size)
        val ratingSc = review.ratingScore.toFloat()


        this.userImage.setImageBitmap(bmp)
        this.usernameRewId.text = review.user.username
        this.ratingRewId.text = java.lang.Double.toString(review.ratingScore)
        this.reviewTextId.text = review.reviewText
        this.reviewDateId.text = review.addedTime.toString()
        this.ratingBarId.rating = ratingSc
    }

    override fun onClick(v: View) {

    }

}
