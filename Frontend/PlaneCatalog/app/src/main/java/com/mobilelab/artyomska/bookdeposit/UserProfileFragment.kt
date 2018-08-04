package com.mobilelab.artyomska.bookdeposit

import android.app.ProgressDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.mobilelab.artyomska.bookdeposit.listAdapter.BookAdapter
import com.mobilelab.artyomska.bookdeposit.model.Author
import com.mobilelab.artyomska.bookdeposit.model.Book
import com.mobilelab.artyomska.bookdeposit.model.Genre
import com.mobilelab.artyomska.bookdeposit.model.UserData
import com.mobilelab.artyomska.bookdeposit.utils.Constants.URL
import com.mobilelab.artyomska.bookdeposit.utils.EndlessRecyclerViewScrollListener
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class UserProfileFragment : Fragment() {

    private var adapter: BookAdapter? = null
    private var llm: LinearLayoutManager? = null

    private var changeProfileData : Button? = null
    private  var addFriendProfileDetail : Button? = null
    private var imageProfileDetail : ImageView? = null
    private var usernameProfileDetail : TextView? = null
    private  var joinDateProfileDetail : TextView? = null
    private  var aboutMeProfileDetail : TextView? = null
    private var viewFriendsProfileDetail : Button? = null
    private var readProfileDetail : Button? = null
    private var bookListProfileDetail : RecyclerView? = null
    private var viewUserReview: Button? = null
    private var userID : String? = null
    private var ID : String? = null
    private var mContext : Context? = null
    private var mainActivity : MainActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        
        val RootView = inflater.inflate(R.layout.profile_layout, container, false)

        mainActivity = activity as MainActivity?
        imageProfileDetail = RootView.findViewById(R.id.imageProfileDetail)
        usernameProfileDetail = RootView.findViewById(R.id.usernameProfileDetail)
        joinDateProfileDetail = RootView.findViewById(R.id.joinDateProfileDetail)
        aboutMeProfileDetail = RootView.findViewById(R.id.aboutMeProfileDetail)

        addFriendProfileDetail = RootView.findViewById(R.id.addFriendProfileDetail)
        viewFriendsProfileDetail = RootView.findViewById(R.id.viewFriendsProfileDetail)
        readProfileDetail = RootView.findViewById(R.id.readProfileDetail)
        bookListProfileDetail = RootView.findViewById(R.id.bookListProfileDetail)
        changeProfileData = RootView.findViewById(R.id.changeProfileData)
        viewUserReview = RootView.findViewById(R.id.viewUserReview)

        llm = LinearLayoutManager(activity)
        bookListProfileDetail!!.layoutManager = llm

        userID = arguments?.getString("userID")
        ID = arguments?.getString("ID")


        if (userID == null)
        {
            getUser(ID)
            changeProfileData!!.visibility = View.GONE

            viewFriendsProfileDetail!!.setOnClickListener {
                val myFrag = MainActivityTab2()
                val bundle = Bundle()
                bundle.putString("userID", ID)
                myFrag.arguments = bundle

                activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, myFrag)
                        ?.addToBackStack(null)
                        ?.commit()
            }

            viewUserReview!!.setOnClickListener {
                val myFrag = ReviewsOfUsersFragment()
                val bundle = Bundle()
                bundle.putString("userID", ID)
                myFrag.arguments = bundle

                activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, myFrag)
                        ?.addToBackStack(null)
                        ?.commit()
            }


            readProfileDetail?.setOnClickListener { v ->
                val popup = PopupMenu(activity, v)
                popup.menuInflater.inflate(R.menu.get_shelve_menu, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {

                        R.id.getToRead ->
                        {
                            val scrollListener: EndlessRecyclerViewScrollListener
                            scrollListener = object : EndlessRecyclerViewScrollListener(llm) {
                                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                                    getBooks(page,ID, "TOREAD")
                                }
                            }

                            adapter = BookAdapter(activity, R.layout.book_item_layout)
                            bookListProfileDetail!!.adapter = adapter
                            bookListProfileDetail!!.addOnScrollListener(scrollListener)
                            scrollListener.resetState()

                            getBooks(0,ID, "TOREAD")
                        }

                        R.id.getAlreadyRead ->
                        {
                            val scrollListener: EndlessRecyclerViewScrollListener
                            scrollListener = object : EndlessRecyclerViewScrollListener(llm) {
                                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                                    getBooks(page,ID, "READ")
                                }
                            }

                            adapter = BookAdapter(activity, R.layout.book_item_layout)
                            bookListProfileDetail!!.adapter = adapter
                            bookListProfileDetail!!.addOnScrollListener(scrollListener)
                            scrollListener.resetState()
                            getBooks(0,ID, "READ")
                        }


                        else -> {
                        }
                    }
                    true
                }
            }
            
            addFriendProfileDetail!!.setOnClickListener {
                addFriend(mainActivity?.loggedUserId, ID)
            }
        } else {
            getUser(userID)
            addFriendProfileDetail!!.visibility = View.GONE

            changeProfileData!!.setOnClickListener {
                val myFrag = ChangeProfileFragment()
                val bundle = Bundle()
                bundle.putString("userID", userID)
                myFrag.arguments = bundle

                activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, myFrag)
                        ?.addToBackStack(null)
                        ?.commit()
            }

            viewUserReview!!.setOnClickListener {
                val myFrag = ReviewsOfUsersFragment()
                val bundle = Bundle()
                bundle.putString("userID", userID)
                myFrag.arguments = bundle

                activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, myFrag)
                        ?.addToBackStack(null)
                        ?.commit()
            }

            readProfileDetail?.setOnClickListener { v ->
                val popup = PopupMenu(activity, v)
                popup.menuInflater.inflate(R.menu.get_shelve_menu, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {

                        R.id.getToRead ->
                        {

                            val scrollListener: EndlessRecyclerViewScrollListener
                            scrollListener = object : EndlessRecyclerViewScrollListener(llm) {
                                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                                    getBooks(page,userID, "TOREAD")
                                }
                            }

                            adapter = BookAdapter(activity, R.layout.book_item_layout)
                            bookListProfileDetail!!.adapter = adapter
                            bookListProfileDetail!!.addOnScrollListener(scrollListener)
                            scrollListener.resetState()

                            getBooks(0,userID, "TOREAD")
                        }

                        R.id.getAlreadyRead ->
                        {
                            val scrollListener: EndlessRecyclerViewScrollListener
                            scrollListener = object : EndlessRecyclerViewScrollListener(llm) {
                                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                                    getBooks(page,userID, "READ")
                                }
                            }

                            adapter = BookAdapter(activity, R.layout.book_item_layout)
                            bookListProfileDetail!!.adapter = adapter
                            bookListProfileDetail!!.addOnScrollListener(scrollListener)
                            scrollListener.resetState()
                            getBooks(0,userID, "READ")
                        }


                        else -> {
                        }
                    }
                    true
                }
            }

            viewFriendsProfileDetail!!.setOnClickListener {
                val myFrag = MainActivityTab2()
                val bundle = Bundle()
                bundle.putString("userID", userID)
                myFrag.arguments = bundle

                activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, myFrag)
                        ?.addToBackStack(null)
                        ?.commit()
            }
        }

        return RootView
    }

    private fun getBooks(page: Int, idToSearch : String?, bookShelf : String) {


        val tag_json_obj = "json_obj_req"
        val uri = String.format("$URL/book/allShelfPaginated?pageNumber=%1\$s&_pageSize=%2\$s&pageSize=%3\$s&userID=%4\$s&bookShelf=%5\$s",
                page, 5, 5, idToSearch, bookShelf)

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
            Log.e("ERROR", "Error occurred ", error)

            val snackbar2 = Snackbar.make(activity!!.window.decorView.rootView, "Server error", Snackbar.LENGTH_LONG).setDuration(2000)
            snackbar2.show()

            /*
            val errorButton = Button(activity)
            errorButton.text = "Retry"
            val ll = activity!!.findViewById<RecyclerView>(R.id.bookListProfileDetail)
            val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            ll.addView(errorButton, lp)

            errorButton.setOnClickListener { v ->
                getBooks(page, idToSearch, bookShelf)
                val parentView = v.parent as ViewGroup
                parentView.removeView(v)
            }
            */
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

    private fun addFriend(loggedUserId: Int?, id: String?) {

        val pDialog = ProgressDialog(activity)
        pDialog.setMessage("Loading...")
        pDialog.show()


        val tag_json_obj = "json_obj_req"
        val url = "$URL/userdata/AddFriend"
        val strReq = object : StringRequest(Request.Method.POST, url,
                Response.Listener { response ->
                    pDialog.dismiss()
                    if (response.toString() == "true")
                    {
                        val snackbar2 = Snackbar.make(activity!!.window.decorView.rootView, "Friend was added", Snackbar.LENGTH_LONG).setDuration(2000)
                        snackbar2.show()

                        addFriendProfileDetail?.text = "Delete Friend"
                        addFriendProfileDetail!!.setOnClickListener {
                            deleteFriend(loggedUserId, id)
                        }

                    }
                    else
                    {
                        val snackbar2 = Snackbar.make(activity!!.window.decorView.rootView, "Friend was not added.", Snackbar.LENGTH_LONG).setDuration(2000)
                        snackbar2.show()
                    }

                }, Response.ErrorListener { error ->
            pDialog.dismiss()
            Log.e("ERROR", "Error occurred ", error)
            val snackbar2 = Snackbar.make(activity!!.window.decorView.rootView, "Server error", Snackbar.LENGTH_LONG).setDuration(2000)
            snackbar2.show()
        })
        {
            override fun getHeaders(): Map<String, String> {
                val settings = PreferenceManager.getDefaultSharedPreferences(activity)
                val auth_token_string = settings.getString("token", "")

                val params = HashMap<String, String>()
                params["Authorization"] = "Basic " + auth_token_string!!
                return params
            }

            override fun getParams(): HashMap<String?, String?> {
                val params = HashMap<String?, String?>()
                params["mainUserID"] = loggedUserId.toString()
                params["secondUserID"] = id

                return params
            }
        }

        strReq.retryPolicy = DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj)
    }


    private fun deleteFriend(loggedUserId: Int?, id: String?) {

        val pDialog = ProgressDialog(activity)
        pDialog.setMessage("Loading...")
        pDialog.show()

        val tag_json_obj = "json_obj_req"
        val url = "$URL/userdata/DeleteFriend"
        val strReq = object : StringRequest(Request.Method.POST, url,
                Response.Listener { response ->
                    pDialog.dismiss()
                    if (response.toString() == "true")
                    {
                        val snackbar2 = Snackbar.make(activity!!.window.decorView.rootView, "Friend was deleted", Snackbar.LENGTH_LONG).setDuration(2000)
                        snackbar2.show()

                        addFriendProfileDetail?.text = "Add Friend"
                        addFriendProfileDetail!!.setOnClickListener {
                            addFriend(loggedUserId, id)
                        }

                    }
                    else
                    {
                        val snackbar2 = Snackbar.make(activity!!.window.decorView.rootView, "Friend was not deleted.", Snackbar.LENGTH_LONG).setDuration(2000)
                        snackbar2.show()
                    }

                }, Response.ErrorListener { error ->
            pDialog.dismiss()
            Log.e("ERROR", "Error occurred ", error)
            val snackbar2 = Snackbar.make(activity!!.window.decorView.rootView, "Server error", Snackbar.LENGTH_LONG).setDuration(2000)
            snackbar2.show()
        })
        {
            override fun getHeaders(): Map<String, String> {
                val settings = PreferenceManager.getDefaultSharedPreferences(activity)
                val auth_token_string = settings.getString("token", "")

                val params = HashMap<String, String>()
                params["Authorization"] = "Basic " + auth_token_string!!
                return params
            }

            override fun getParams(): HashMap<String?, String?> {
                val params = HashMap<String?, String?>()
                params["mainUserID"] = loggedUserId.toString()
                params["secondUserID"] = id

                return params
            }
        }

        strReq.retryPolicy = DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj)
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }


    private fun getUser(id : String?) {




        val params = HashMap<String?, String?>()
        params["ID"] = id
        params["username"] = mainActivity?.loggedUserId.toString()

        val tag_json_obj = "json_obj_req"
        val url = "$URL/userdata/GetUserAfterID"
        val strReq = object : JsonObjectRequest(url, JSONObject(params),
                Response.Listener { response ->
                        val newUser = UserData()

                        val dateRez = response.getString("joinedDate").substring(0, response.getString("joinedDate").indexOf('T'))
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
                        val relDate = LocalDate.parse(dateRez, formatter)

                        newUser.id = Integer.parseInt(response.getString("ID"))
                        newUser.username = response.getString("username")
                        newUser.userPic = response.getString("userPic").toByteArray()
                        newUser.userOverview = response.getString("userOverview")
                        newUser.joinedDate = relDate

                        usernameProfileDetail?.text = newUser.username
                        aboutMeProfileDetail?.text = newUser.userOverview
                        joinDateProfileDetail?.text = newUser.joinedDate.toString()


                        val x = Base64.decode(newUser.userPic, Base64.DEFAULT)
                        val bmp = BitmapFactory.decodeByteArray(x, 0, x.size)

                        val userLogged : JSONObject = response.getJSONObject("userLogged");
                        if (userLogged.getString("ID").toInt() != 0)
                        {
                            addFriendProfileDetail?.text = "Delete Friend"
                            addFriendProfileDetail!!.setOnClickListener {
                                deleteFriend(userLogged.getString("ID").toInt(), id)
                            }
                        }
                        imageProfileDetail?.setImageBitmap(bmp)

                }, Response.ErrorListener { error ->
            Log.e("ERROR", "Error occurred ", error)
            val snackbar2 = Snackbar.make(activity!!.window.decorView.rootView, "Server error", Snackbar.LENGTH_LONG).setDuration(2000)
            snackbar2.show()
        })
        {
            override fun getHeaders(): Map<String, String> {
                val settings = PreferenceManager.getDefaultSharedPreferences(activity)
                val auth_token_string = settings.getString("token", "")

                val params = HashMap<String, String>()
                params["Authorization"] = "Basic " + auth_token_string!!
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