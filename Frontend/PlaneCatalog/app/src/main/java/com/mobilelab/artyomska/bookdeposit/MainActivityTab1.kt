package com.mobilelab.artyomska.bookdeposit

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import com.android.volley.*

import com.android.volley.toolbox.JsonArrayRequest
import com.mobilelab.artyomska.bookdeposit.model.Author
import com.mobilelab.artyomska.bookdeposit.model.Book
import com.mobilelab.artyomska.bookdeposit.model.Genre
import com.mobilelab.artyomska.bookdeposit.listAdapter.BookAdapter
import com.mobilelab.artyomska.bookdeposit.utils.EndlessRecyclerViewScrollListener

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import java.util.HashMap
import java.util.Locale

import com.mobilelab.artyomska.bookdeposit.utils.Constants.URL

class MainActivityTab1 : Fragment() {

    private var adapter: BookAdapter? = null
    private var activity: MainActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity = getActivity() as MainActivity?

        val RootView = inflater.inflate(R.layout.tab1, container, false)

        val bookList = RootView.findViewById<RecyclerView>(R.id.bookList)

        val llm = LinearLayoutManager(activity)
        bookList.layoutManager = llm

        val scrollListener: EndlessRecyclerViewScrollListener
        scrollListener = object : EndlessRecyclerViewScrollListener(llm) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                getBooksFromServer(page, null, null, null)
            }
        }

        adapter = BookAdapter(activity, R.layout.book_item_layout)
        bookList.adapter = adapter
        bookList.addOnScrollListener(scrollListener)
        scrollListener.resetState()
        getBooksFromServer(0, null, null, null)

        setHasOptionsMenu(true)
        return RootView

    }


    override fun onPrepareOptionsMenu(menu : Menu) {

        val searchView : SearchView = menu.findItem(R.id.menu_search).actionView as SearchView;

        searchView.setOnQueryTextListener(null);
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {

                if (searchView.width > 0) {
                    val myFrag = SearchFragment()
                    val bundle = Bundle()
                    bundle.putString("searchQuery", query)
                    myFrag.arguments = bundle


                    activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.fragment_container, myFrag)
                            ?.addToBackStack(null)
                            ?.commit()

                    return true
                }
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                return true
            }
        })



        super.onPrepareOptionsMenu(menu);
    }


    private fun getBooksFromServer(page: Int, filterAfter: String?, filterField: String?, sortMethod: String?) {
        val tag_json_obj = "json_obj_req"
        val uri = String.format("$URL/book/allPagined?pageNumber=%1\$s&_pageSize=%2\$s&pageSize=%3\$s&filterAfter=%4\$s&filterField=%5\$s&sortMethod=%6\$s",
                page, 5, 5, filterAfter, filterField, sortMethod)

        val jsonObjReq = object : JsonArrayRequest(Request.Method.GET, uri, null, Response.Listener { response ->
            val newList = ArrayList<Book>()
                for (i in 0 until response.length()) {
                    val author = ArrayList<Author>()
                    val genre = ArrayList<Genre>()

                    val pl = response.getJSONObject(i)
                    val arrayAuth = pl.getJSONArray("author")
                    val arrayGenre = pl.getJSONArray("genre")

                    val dateRez = pl.getString("releaseDate").substring(0, pl.getString("releaseDate").indexOf('T'))
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
                    val relDate = LocalDate.parse(dateRez, formatter)

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
                    newBook.bookPic = pl.getString("bookPic").toByteArray()
                    newBook.id = Integer.parseInt(pl.getString("ID"))
                    newBook.title = pl.getString("title")

                    newBook.genre = genre
                    newBook.author = author
                    newBook.rating = java.lang.Double.parseDouble(pl.getString("rating"))
                    newBook.releaseDate = relDate
                    newList.add(newBook)
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
            val ll = getActivity()!!.findViewById<RelativeLayout>(R.id.layoutIDTab1)
            val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            ll.addView(errorButton, lp)

            errorButton.setOnClickListener { v ->
                getBooksFromServer(page, filterAfter, filterField, sortMethod)
                val parentView = v.parent as ViewGroup
                parentView.removeView(v)
            }
        }) {

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
