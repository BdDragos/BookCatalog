package com.mobilelab.artyomska.bookdeposit

import android.app.ProgressDialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView

import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.mobilelab.artyomska.bookdeposit.model.Rating
import com.mobilelab.artyomska.bookdeposit.model.Review

import java.time.LocalDate
import java.util.HashMap
import java.util.Objects

import com.mobilelab.artyomska.bookdeposit.utils.Constants.URL


class WriteReviewFragment : Fragment() {

    private var ratingBar: RatingBar? = null
    private var ratingScore: TextView? = null
    private var addButton: Button? = null
    private var writeText: TextView? = null
    private var bookID: String? = null
    private var userID: String? = null
    private var activity: MainActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity = getActivity() as MainActivity?

        bookID = Objects.requireNonNull<Bundle>(arguments).getString("bookID")
        userID = arguments!!.getString("userID")

        val RootView = inflater.inflate(R.layout.fragment_write_review, container, false)

        ratingBar = RootView.findViewById(R.id.ratingBarWriteReview)
        writeText = RootView.findViewById(R.id.reviewWriteText)
        addButton = RootView.findViewById(R.id.submitReviewButton)
        ratingScore = RootView.findViewById(R.id.ratingScoreWriteReview)

        ratingBar!!.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->

            val rateValue = ratingBar.rating.toString()
            ratingScore!!.text = rateValue
        }

        addButton!!.setOnClickListener {
            val rateValue = ratingBar!!.rating.toString()
            val text = writeText!!.text.toString()

            if (text.length > 2000) {
                val alertDialog = AlertDialog.Builder(activity!!, R.style.AppTheme_PopupOverlay).create()
                alertDialog.setTitle("Error")
                alertDialog.setMessage("The text should have less than 2000 characters")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            } else {
                val rating = Rating()
                rating.bookID = Integer.parseInt(bookID)
                rating.userID = Integer.parseInt(userID)
                rating.ratingScore = java.lang.Double.parseDouble(rateValue)
                val review = Review()
                review.bookID = Integer.parseInt(bookID)
                review.userID = Integer.parseInt(userID)
                review.addedTime = LocalDate.now()
                review.reviewText = text
                review.ratingScore = rating.ratingScore

                addReviewAndRating(review)
            }
        }

        return RootView
    }


    private fun addReviewAndRating(review: Review) {
        val pDialog = ProgressDialog(getActivity())
        pDialog.setMessage("Loading...")
        pDialog.show()

        val tag_json_obj = "json_obj_req"
        val url = "$URL/review/AddReviewAndRating"
        val strReq = object : StringRequest(Request.Method.POST, url,
                Response.Listener { response ->
                    pDialog.dismiss()
                    if (response.compareTo("false") != 0) {
                        val alertDialog = AlertDialog.Builder(activity!!, R.style.AppTheme_PopupOverlay).create()
                        alertDialog.setTitle("Success")
                        alertDialog.setMessage("The review and the rating were added")
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                        ) { dialog, which -> dialog.dismiss() }
                        alertDialog.show()

                        activity!!.supportFragmentManager.popBackStack()
                    } else {
                        val alertDialog = AlertDialog.Builder(activity!!, R.style.AppTheme_PopupOverlay).create()
                        alertDialog.setTitle("Error")
                        alertDialog.setMessage("A review by this user already exists")
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                        ) { dialog, which -> dialog.dismiss() }
                        alertDialog.show()

                        activity!!.supportFragmentManager.popBackStack()
                    }
                }, Response.ErrorListener { error ->
            pDialog.dismiss()
            Log.e("ERROR", "Error occurred ", error)
            val ve = getActivity()!!.window.decorView.rootView
            if (ve != null) {
                val snackbar2 = Snackbar.make(ve, error.toString(), Snackbar.LENGTH_LONG).setDuration(2000)
                snackbar2.show()
            }
        }) {
            override fun getHeaders(): Map<String, String> {
                val settings = PreferenceManager.getDefaultSharedPreferences(getActivity())
                val auth_token_string = settings.getString("token", "")

                val params = HashMap<String, String>()
                params["Authorization"] = "Basic " + auth_token_string!!
                return params
            }

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["reviewText"] = review.reviewText
                params["bookID"] = Integer.toString(review.bookID)
                params["userID"] = Integer.toString(review.userID)
                params["ratingScore"] = java.lang.Double.toString(review.ratingScore)

                return params
            }

        }

        strReq.retryPolicy = DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj)
    }
}
