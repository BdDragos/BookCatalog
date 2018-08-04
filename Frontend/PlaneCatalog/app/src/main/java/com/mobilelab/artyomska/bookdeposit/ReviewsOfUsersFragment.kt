package com.mobilelab.artyomska.bookdeposit

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.mobilelab.artyomska.bookdeposit.listAdapter.ReviewsOfUserAdapter
import com.mobilelab.artyomska.bookdeposit.model.*
import com.mobilelab.artyomska.bookdeposit.utils.Constants
import com.mobilelab.artyomska.bookdeposit.utils.EndlessRecyclerViewScrollListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ReviewsOfUsersFragment : Fragment() {

    private var adapter: ReviewsOfUserAdapter? = null
    private var activity: MainActivity? = null
    private var userID: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity = getActivity() as MainActivity?

        val RootView = inflater.inflate(R.layout.reviews_of_users_layout, container, false)

        val reviewsList = RootView.findViewById<RecyclerView>(R.id.reviewsUserList)

        userID = arguments!!.getString("userID")

        val llm = LinearLayoutManager(activity)
        reviewsList.layoutManager = llm

        val scrollListener: EndlessRecyclerViewScrollListener
        scrollListener = object : EndlessRecyclerViewScrollListener(llm) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                getReviewsOfUser(page, null, null, null)
            }
        }

        adapter = ReviewsOfUserAdapter(activity)
        reviewsList.adapter = adapter
        reviewsList.addOnScrollListener(scrollListener)
        scrollListener.resetState()
        getReviewsOfUser(0, null, null, null)

        setHasOptionsMenu(true)
        return RootView
    }

    private fun getReviewsOfUser(page: Int, filterAfter: String?, filterField: String?, sortMethod: String?) {
        val tag_json_obj = "json_obj_req"
        val uri = String.format(Constants.URL + "/review/allPaginedAfterUserID?pageNumber=%1\$s&_pageSize=%2\$s&pageSize=%3\$s&filterAfter=%4\$s&filterField=%5\$s&sortMethod=%6\$s&IDURL=%7\$s",
                page, 5, 5, filterAfter, filterField, sortMethod, userID)

        val jsonObjReq = object : JsonArrayRequest(Request.Method.GET, uri, null, Response.Listener { response ->
            val newList = ArrayList<Review>()
            for (i in 0 until response.length()) {

                val pl = response.getJSONObject(i)

                val dateRez = pl.getString("addedTime").substring(0, pl.getString("addedTime").indexOf('T'))
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
                val relDate = LocalDate.parse(dateRez, formatter)

                val review = Review()
                review.id = Integer.parseInt(pl.getString("ID"))
                review.addedTime = relDate
                review.ratingScore = java.lang.Double.parseDouble(pl.getString("ratingScore"))
                review.bookID = Integer.parseInt(pl.getString("bookID"))
                review.userID = Integer.parseInt(pl.getString("userID"))
                review.reviewText = pl.getString("reviewText")
                val userData = pl.getJSONObject("user")
                val parsedUser = UserData(Integer.parseInt(userData.getString("ID")), userData.getString("username"), userData.getString("email"), userData.getString("userPic").toByteArray())
                review.user = parsedUser

                val author = ArrayList<Author>()
                val genre = ArrayList<Genre>()

                val bk = pl.getJSONObject("book")

                val arrayAuth = bk.getJSONArray("author")
                val arrayGenre = bk.getJSONArray("genre")

                val dateRezBook = bk.getString("releaseDate").substring(0, bk.getString("releaseDate").indexOf('T'))
                val relDateBook = LocalDate.parse(dateRezBook, formatter)

                for (n in 0 until arrayAuth.length()) {
                    val `object` = arrayAuth.getJSONObject(n)
                    val auth = Author(Integer.parseInt(`object`.getString("ID")), `object`.getString("authorName"))
                    author.add(auth)
                }

                for (n in 0 until arrayGenre.length()) {
                    val `object` = arrayGenre.getJSONObject(n)
                    val gen = Genre(Integer.parseInt(`object`.getString("ID")), `object`.getString("genreName"))
                    genre.add(gen)
                }

                val newBook = Book()
                newBook.bookPic = bk.getString("bookPic").toByteArray()
                newBook.id = Integer.parseInt(bk.getString("ID"))
                newBook.title = bk.getString("title")

                newBook.genre = genre
                newBook.author = author
                newBook.rating = java.lang.Double.parseDouble(bk.getString("rating"))
                newBook.releaseDate = relDateBook

                review.book = newBook;

                newList.add(review)
                newList.add(review)
            }

            adapter!!.addNewDataPage(newList)

        }, Response.ErrorListener { error ->

            val response : NetworkResponse? = error?.networkResponse
            if (response?.statusCode == 401)
            {
                val settings = PreferenceManager.getDefaultSharedPreferences(activity)
                val editor = settings.edit()
                editor.clear()
                editor.putBoolean("hasLoggedIn", false)
                editor.apply()
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish();
            }

            Log.e("ERROR", "Error occurred ", error)

            val snackbar2 = Snackbar.make(activity!!.window.decorView.rootView, "Server error", Snackbar.LENGTH_LONG).setDuration(2000)
            snackbar2.show()



            val errorButton = Button(activity)
            errorButton.text = "Retry"
            val ll = getActivity()!!.findViewById<RelativeLayout>(R.id.reviewsOfUsersLayout)
            val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            ll.addView(errorButton, lp)

            errorButton.setOnClickListener { v ->
                getReviewsOfUser(page, filterAfter, filterField, sortMethod)
                val parentView = v.parent as ViewGroup
                parentView.removeView(v)
            }
        })
        {
            override fun getHeaders(): Map<String, String> {
                val settings = PreferenceManager
                        .getDefaultSharedPreferences(activity)
                val auth_token_string = settings.getString("token", "")

                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json; charset=UTF-8"
                params["Authorization"] = "Basic " + auth_token_string!!
                return params
            }
        }

        jsonObjReq.retryPolicy = DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj)
    }
}