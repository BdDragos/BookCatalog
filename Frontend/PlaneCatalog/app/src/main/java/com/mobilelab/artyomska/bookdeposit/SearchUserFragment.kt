package com.mobilelab.artyomska.bookdeposit

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.mobilelab.artyomska.bookdeposit.listAdapter.UserAdapter
import com.mobilelab.artyomska.bookdeposit.model.UserData
import com.mobilelab.artyomska.bookdeposit.utils.Constants
import com.mobilelab.artyomska.bookdeposit.utils.EndlessRecyclerViewScrollListener
import java.util.*

class SearchUserFragment : Fragment() {

    var adapter: UserAdapter? = null
    var activity: MainActivity? = null
    var userID: Int? = 0
    var descriptConstraintLayout : RelativeLayout? = null
    var userList : RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        activity = getActivity() as MainActivity?

        val RootView = inflater.inflate(R.layout.tab2, container, false)

        userList = RootView.findViewById(R.id.friendList)

        setHasOptionsMenu(true)

        val llm = LinearLayoutManager(activity)
        userList?.layoutManager = llm

        val searchQuery = arguments!!.getString("searchQuery")
        val searchForUserText = RootView.findViewById<EditText>(R.id.searchForUserText)
        descriptConstraintLayout = RootView.findViewById(R.id.layoutIDTab2)


        userID = activity?.loggedUserId

        val scrollListener: EndlessRecyclerViewScrollListener
        scrollListener = object : EndlessRecyclerViewScrollListener(llm) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                getFriendsFromServer(page,userID, searchQuery)
            }
        }

        searchForUserText.setOnKeyListener { v, keyCode, event ->
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
            {
                val rez = searchForUserText.text.toString()
                if (searchForUserText.textSize >= 1)
                {
                    val myFrag = SearchUserFragment()
                    val bundle = Bundle()
                    bundle.putString("searchQuery", rez)
                    bundle.putString("userID", userID.toString())
                    myFrag.arguments = bundle

                    activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.fragment_container, myFrag)
                            ?.addToBackStack(null)
                            ?.commit()
                }
                true
            } else {
                false
            }
        }

        adapter = UserAdapter(activity, R.layout.user_item_layout, scrollListener)
        userList?.adapter = adapter
        userList?.addOnScrollListener(scrollListener)
        scrollListener.resetState()
        getFriendsFromServer(0,userID,searchQuery)
        return RootView

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<View>(R.id.toolbar) as Toolbar?
        (getActivity() as MainActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
    }


    private fun getFriendsFromServer(page: Int, userID: Int?, searchQuery: String) {
        val tag_json_obj = "json_obj_req"
        val uri = String.format("${Constants.URL}/userdata/getSearch?pageNumber=%1\$s&_pageSize=%2\$s&pageSize=%3\$s&userID=%4\$s&username=%5\$s",
                page, 5, 5, userID,searchQuery)

        val jsonObjReq = object : JsonArrayRequest(Request.Method.GET, uri, null, Response.Listener { response ->
            val newList = ArrayList<UserData>()
            if (response.length() == 0 && page == 0)
            {
                userList?.visibility = View.GONE
            }
            else
            {
                for (i in 0 until response.length()) {

                    val pl = response.getJSONObject(i)
                    val newUser = UserData()
                    newUser.userPic = pl.getString("userPic").toByteArray()
                    newUser.id = Integer.parseInt(pl.getString("ID"))
                    newUser.username = pl.getString("username")
                    newUser.noOfFriends = pl.getString("noOfFriends")
                    newList.add(newUser)
                }
            }

            adapter?.addNewDataPage(newList)

        }, Response.ErrorListener { error ->
            Log.e("ERROR", "Error occurred ", error)

            val win = Objects.requireNonNull(activity?.window?.decorView?.rootView)

            if (win != null) {
                val snackbar2 = Snackbar.make(win, "Server error", Snackbar.LENGTH_LONG).setDuration(2000)
                snackbar2.show()
            }


            val errorButton = Button(activity)
            errorButton.text = "Retry"
            val ll = getActivity()!!.findViewById<RelativeLayout>(R.id.layoutIDTab2)
            val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            ll.addView(errorButton, lp)

            errorButton.setOnClickListener { v ->
                getFriendsFromServer(page,userID,searchQuery)
                val parentView = v.parent as ViewGroup
                parentView.removeView(v)
            }
        }) {
            @Throws(AuthFailureError::class)
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
