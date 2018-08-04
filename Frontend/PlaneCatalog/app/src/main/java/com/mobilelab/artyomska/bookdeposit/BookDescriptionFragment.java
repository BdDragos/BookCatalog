package com.mobilelab.artyomska.bookdeposit;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.mobilelab.artyomska.bookdeposit.listAdapter.ReviewAdapter;
import com.mobilelab.artyomska.bookdeposit.model.Author;
import com.mobilelab.artyomska.bookdeposit.model.Book;
import com.mobilelab.artyomska.bookdeposit.model.Genre;
import com.mobilelab.artyomska.bookdeposit.model.Review;
import com.mobilelab.artyomska.bookdeposit.model.UserData;
import com.mobilelab.artyomska.bookdeposit.utils.Constants;
import com.mobilelab.artyomska.bookdeposit.utils.EndlessRecyclerViewScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class BookDescriptionFragment extends Fragment {
    
    private ReviewAdapter adapter;
    private RecyclerView reviewList;
    private LinearLayoutManager llm;
    private ImageView thumbNail;
    private TextView title;
    private TextView rating;
    private TextView genre;
    private TextView author;
    private TextView year;
    private TextView isbn;
    private TextView noPage;
    private TextView edition;
    private TextView bLanguage;
    private TextView publisher;
    private TextView publisherSite;
    private TextView bookFormat;
    private TextView overview;
    private TextView initialYear;
    private ConstraintLayout descriptConstraintLayout;
    private String bookID;
    private String bookISBN;
    private Button addReviewButton;
    private MainActivity activity;
    private Book book;
    private String loggedUserID;
    private Button addToShelfButton;
    private Button deleteFromShelfDetail;
    private ConstraintLayout layourForMyReview;
    private Button deleteReviewBut;
    private TextView seriesBookDesc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        activity = (MainActivity) getActivity();

        View RootView = inflater.inflate(R.layout.fragment_book_description, container, false);

        reviewList = RootView.findViewById(R.id.reviewListId);

        llm = new LinearLayoutManager(activity);
        reviewList.setLayoutManager(llm);

        EndlessRecyclerViewScrollListener scrollListener;
        scrollListener = new EndlessRecyclerViewScrollListener(llm)
        {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getReviewFromServer(page,null,null,null);
            }
        };

        bookID = Objects.requireNonNull(getArguments()).getString("ID");
        bookISBN = getArguments().getString("isbn");
        loggedUserID = Integer.toString(activity.getLoggedUserId());
        addToShelfButton = RootView.findViewById(R.id.addToShelfButton);
        thumbNail = RootView.findViewById(R.id.bookCoverId);
        title = RootView.findViewById(R.id.titleId);
        rating = RootView.findViewById(R.id.ratingDetailID);
        genre = RootView.findViewById(R.id.genreId);
        author = RootView.findViewById(R.id.authorId);
        year = RootView.findViewById(R.id.releaseDateId);
        initialYear = RootView.findViewById(R.id.initialReleaseDateId);
        isbn  = RootView.findViewById(R.id.isbnId);
        noPage  = RootView.findViewById(R.id.pageId);
        edition  = RootView.findViewById(R.id.editionId);
        bLanguage  = RootView.findViewById(R.id.languageId);
        publisher = RootView.findViewById(R.id.publisherId);
        publisherSite  = RootView.findViewById(R.id.publisherSiteId);
        bookFormat  = RootView.findViewById(R.id.formatId);
        overview  = RootView.findViewById(R.id.overviewId);
        descriptConstraintLayout = RootView.findViewById(R.id.descriptConstraintLayout);
        deleteFromShelfDetail = RootView.findViewById(R.id.deleteFromShelfDetail);
        addReviewButton = RootView.findViewById(R.id.addReviewId);
        deleteReviewBut = RootView.findViewById(R.id.deleteReviewBut);
        seriesBookDesc = RootView.findViewById(R.id.seriesBookDesc);

        layourForMyReview = RootView.findViewById(R.id.layoutForMyReview);

        getLayoutInflater().inflate(R.layout.review_item_layout, layourForMyReview);

        adapter = new ReviewAdapter(activity, R.layout.review_item_layout,scrollListener);
        reviewList.setAdapter(adapter);
        reviewList.addOnScrollListener(scrollListener);

        deleteFromShelfDetail.setVisibility(View.GONE);

        publisherSite.setOnClickListener(new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            String query = null;
            try {
                query = URLEncoder.encode(publisherSite.getText().toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String url = "http://www.google.com/search?q=" + query;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            activity.startActivity(intent);

            }
        });


        addToShelfButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(getActivity(), v);
                popup.getMenuInflater().inflate(R.menu.shelve_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.alreadyRead:

                                addToShelf(loggedUserID,bookID, "READ");

                                break;

                            case R.id.toRead:

                                addToShelf(loggedUserID,bookID, "TOREAD");
                                break;

                            default:
                                break;
                        }
                        return true;
                    }
                });
            }
        });

        addReviewButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                WriteReviewFragment myFrag = new WriteReviewFragment();
                Bundle bundle = new Bundle();
                if (isbn.equals("0"))
                {
                    isbn = null;
                }
                bundle.putString("userID", loggedUserID);
                bundle.putString("bookID", Integer.toString(book.getID()));
                myFrag.setArguments(bundle);

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, myFrag)
                        .addToBackStack(null)
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_out_right, R.anim.slide_in_left)
                        .commit();

            }
        });


        scrollListener.resetState();
        getReviewFromServer(0,null,null,null);
        getBookAfterID();
        return RootView;

    }

    private void deleteFromShelf(final String loggedUserID, final String bookID, final String toread)
    {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        String url = Constants.URL + "/book/DeleteBookFromShelf";
        String tag_json_obj = "json_obj_req";

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.compareTo("true") == 0)
                {
                    Snackbar snackbar2 = Snackbar.make(Objects.requireNonNull(getActivity()).getWindow().getDecorView().getRootView(), "The book was deleted from the shelf", Snackbar.LENGTH_LONG).setDuration(2000);
                    snackbar2.show();

                    deleteFromShelfDetail.setVisibility(View.GONE);



                }
                else {
                    Snackbar snackbar2 = Snackbar.make(Objects.requireNonNull(getActivity()).getWindow().getDecorView().getRootView(), "The book couldn't be deleted from the shelf", Snackbar.LENGTH_LONG).setDuration(2000);
                    snackbar2.show();
                }

                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e("ERROR", "Error occurred ", error);
                Snackbar snackbar2 = Snackbar.make(Objects.requireNonNull(getActivity()).getWindow().getDecorView().getRootView(), "Server error", Snackbar.LENGTH_LONG).setDuration(2000);
                snackbar2.show();
                pDialog.dismiss();
            }
        }) {

            @Override
            public Map<String, String> getHeaders()
            {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String auth_token_string = settings.getString("token", "");

                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Basic " + auth_token_string);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<>();
                params.put("userID", loggedUserID);
                params.put("bookID", bookID);
                params.put("bookShelf", toread);

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }



    private void addToShelf(final String loggedUserID, final String bookID, final String toread)
    {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        String url = Constants.URL + "/book/AddBookToShelf";
        String tag_json_obj = "json_obj_req";

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.compareTo("true") == 0)
                {
                    Snackbar snackbar2 = Snackbar.make(Objects.requireNonNull(getActivity()).getWindow().getDecorView().getRootView(), "The book was added to the shelf", Snackbar.LENGTH_LONG).setDuration(2000);
                    snackbar2.show();

                    deleteFromShelfDetail.setVisibility(View.VISIBLE);
                    deleteFromShelfDetail.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            deleteFromShelf(loggedUserID,bookID,"READ");
                        }
                    });
                }
                else {
                    Snackbar snackbar2 = Snackbar.make(Objects.requireNonNull(getActivity()).getWindow().getDecorView().getRootView(), "The book couldn't be added", Snackbar.LENGTH_LONG).setDuration(2000);
                    snackbar2.show();
                }

                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e("ERROR", "Error occurred ", error);
                Snackbar snackbar2 = Snackbar.make(Objects.requireNonNull(getActivity()).getWindow().getDecorView().getRootView(), "Server error", Snackbar.LENGTH_LONG).setDuration(2000);
                snackbar2.show();
                pDialog.dismiss();
            }
        }) {

            @Override
            public Map<String, String> getHeaders()
            {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String auth_token_string = settings.getString("token", "");

                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Basic " + auth_token_string);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<>();
                params.put("userID", loggedUserID);
                params.put("bookID", bookID);
                params.put("bookShelf", toread);

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }



    private void getOneReview()
    {

        Map<String, String> params = new HashMap<>();
        params.put("ID", loggedUserID);
        params.put("userID", Integer.toString(book.getID()));

        String tag_json_obj = "json_obj_req";
        String url = Constants.URL + "/review/GetOneReview";
        JsonObjectRequest strReq = new JsonObjectRequest (url, new JSONObject(params),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            if (Integer.parseInt(response.getString("ID")) != 0)
                            {
                                String dateRez = response.getString("addedTime").substring(0, response.getString("addedTime").indexOf('T'));
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
                                LocalDate relDate = LocalDate.parse(dateRez, formatter);

                                JSONObject rati = response.getJSONObject("rating");
                                Review review = new Review();
                                review.setID(Integer.parseInt(response.getString("ID")));
                                review.setAddedTime(relDate);
                                review.setRatingScore(Double.parseDouble(response.getString("ratingScore")));
                                review.setBookID(Integer.parseInt(response.getString("bookID")));
                                review.setUserID(Integer.parseInt(response.getString("userID")));
                                review.setReviewText(response.getString("reviewText"));
                                JSONObject userData = response.getJSONObject("user");
                                UserData parsedUser = new UserData(Integer.parseInt(userData.getString("ID")),userData.getString("username"),userData.getString("email"),userData.getString("userPic").getBytes());
                                review.setUser((parsedUser));
                                ImageView userImage = layourForMyReview.findViewById(R.id.userImageRew);
                                TextView usernameRewId = layourForMyReview.findViewById(R.id.usernameRewId);
                                TextView ratingRewId = layourForMyReview.findViewById(R.id.ratingRewId);
                                TextView reviewTextId  = layourForMyReview.findViewById(R.id.reviewTextId);
                                RatingBar ratingBarId  = layourForMyReview.findViewById(R.id.ratingBarId);
                                TextView reviewDateId  = layourForMyReview.findViewById(R.id.reviewDateId);

                                byte[] x = Base64.decode(review.getUser().getUserPic(), Base64.DEFAULT);
                                Bitmap bmp = BitmapFactory.decodeByteArray(x, 0, x.length);
                                float ratingSc = (float) review.getRatingScore();


                                userImage.setImageBitmap(bmp);
                                usernameRewId.setText(review.getUser().getUsername());
                                ratingRewId.setText(Double.toString(review.getRatingScore()));
                                reviewTextId.setText(review.getReviewText());
                                reviewDateId.setText(review.getAddedTime().toString());
                                ratingBarId.setRating(ratingSc);

                                final int reviewIDelete = review.getID();
                                final int ratingIDelete = Integer.parseInt(rati.getString("ID"));
                                addReviewButton.setVisibility(View.INVISIBLE);
                                deleteReviewBut.setVisibility(View.VISIBLE);

                                deleteReviewBut.setOnClickListener(new View.OnClickListener(){

                                    
                                @Override
                                public void onClick(View v) {
                                    deleteReview(reviewIDelete, ratingIDelete);
                                }
                            });

                            }
                            else
                            {
                                layourForMyReview.setVisibility(View.GONE);
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e("ERROR", "Error occurred ", error);
                Snackbar snackbar2 = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), "Server error", Snackbar.LENGTH_LONG).setDuration(2000);
                snackbar2.show();
                layourForMyReview.setVisibility(View.GONE);
            }
        })
        {
            @Override
            public Map<String, String> getHeaders()
            {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String auth_token_string = settings.getString("token", "");

                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Basic " + auth_token_string);
                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void deleteReview(final int reviewIDelete, final int ratingIDelete)
    {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Deleting...");
        pDialog.show();

        String url = Constants.URL + "/review/DeleteReview";
        String tag_json_obj = "json_obj_req";

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.compareTo("true") == 0)
                {
                    Snackbar snackbar2 = Snackbar.make(Objects.requireNonNull(getActivity()).getWindow().getDecorView().getRootView(), "The review was deleted", Snackbar.LENGTH_LONG).setDuration(2000);
                    snackbar2.show();

                    adapter.clearList();
                    getReviewFromServer(0,null,null,null);
                    deleteReviewBut.setVisibility(View.GONE);
                    addReviewButton.setVisibility(View.VISIBLE);
                    layourForMyReview.setVisibility(View.GONE);

                }
                else {
                    Snackbar snackbar2 = Snackbar.make(Objects.requireNonNull(getActivity()).getWindow().getDecorView().getRootView(), "The review couldn't be deleted", Snackbar.LENGTH_LONG).setDuration(2000);
                    snackbar2.show();
                }

                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e("ERROR", "Error occurred ", error);
                Snackbar snackbar2 = Snackbar.make(Objects.requireNonNull(getActivity()).getWindow().getDecorView().getRootView(), "Server error", Snackbar.LENGTH_LONG).setDuration(2000);
                snackbar2.show();
                pDialog.dismiss();
            }
        }) {

            @Override
            public Map<String, String> getHeaders()
            {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String auth_token_string = settings.getString("token", "");

                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Basic " + auth_token_string);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<>();
                params.put("ID", Integer.toString(reviewIDelete));
                params.put("userID", Integer.toString(ratingIDelete));

                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }



    private void getReviewFromServer(final int page, final String filterAfter ,final String filterField,final String sortMethod )
    {
        String tag_json_obj = "json_obj_req";
        String uri = String.format(Constants.URL + "/review/allPaginedAfterID?pageNumber=%1$s&_pageSize=%2$s&pageSize=%3$s&filterAfter=%4$s&filterField=%5$s&sortMethod=%6$s&IDURL=%7$s&isbn=%8$s",
                page, 5, 5, filterAfter,filterField,sortMethod,bookID,bookISBN);


        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, uri, null, new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                ArrayList<Review> newList = new ArrayList<>();
                try
                {
                    for(int i=0;i<response.length();i++) {

                        JSONObject pl = response.getJSONObject(i);

                        String dateRez = pl.getString("addedTime").substring(0, pl.getString("addedTime").indexOf('T'));
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
                        LocalDate relDate = LocalDate.parse(dateRez, formatter);


                        Review review = new Review();
                        review.setID(Integer.parseInt(pl.getString("ID")));
                        review.setAddedTime(relDate);
                        review.setRatingScore(Double.parseDouble(pl.getString("ratingScore")));
                        review.setBookID(Integer.parseInt(pl.getString("bookID")));
                        review.setUserID(Integer.parseInt(pl.getString("userID")));
                        review.setReviewText(pl.getString("reviewText"));
                        JSONObject userData = pl.getJSONObject("user");
                        UserData parsedUser = new UserData(Integer.parseInt(userData.getString("ID")),userData.getString("username"),userData.getString("email"),userData.getString("userPic").getBytes());
                        review.setUser((parsedUser));
                        newList.add(review);
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                adapter.addNewDataPage(newList);

            }

        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e("ERROR", "Error occurred ", error);
                Snackbar snackbar2 = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), "Server error", Snackbar.LENGTH_LONG).setDuration(2000);
                snackbar2.show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String auth_token_string = settings.getString("token", "");

                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Basic " + auth_token_string);
                return params;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void getBookAfterID()
    {
        JSONObject json = new JSONObject();
        try {
            json.put("ID",bookID);
            json.put("isbn",bookISBN);
            json.put("loggedUserID",loggedUserID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String tag_json_obj = "json_obj_req";
        String url = Constants.URL + "/book/getSingleBook";
        JsonObjectRequest strReq = new JsonObjectRequest (Request.Method.POST, url, json,
                new Response.Listener<JSONObject>()
                {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try {
                            if (response == null || response.getString("title") == null || response.getString("title").equals("null"))
                            {
                                if ((descriptConstraintLayout).getChildCount() > 0) {
                                    (descriptConstraintLayout).removeAllViews();
                                    TextView tv0 = new TextView(getContext());
                                    tv0.setText("No book which contains the given ID/ISBN found");
                                    descriptConstraintLayout.addView(tv0);
                                }
                            }
                            else {
                                    List<Author> authorParse = new ArrayList<>();
                                    List<Genre> genreParse = new ArrayList<>();

                                    JSONObject userObject = response.getJSONObject("user");
                                    JSONArray arrayAuth = response.getJSONArray("author");
                                    JSONArray arrayGenre = response.getJSONArray("genre");

                                    bookID = response.getString("ID");

                                    if (!userObject.getString("ID").equals("null") && !userObject.getString("ID").equals("0"))
                                    {
                                        deleteFromShelfDetail.setVisibility(View.VISIBLE);
                                        deleteFromShelfDetail.setOnClickListener(new View.OnClickListener(){

                                            @Override
                                            public void onClick(View v) {
                                                deleteFromShelf(loggedUserID,bookID,"READ");
                                            }
                                        });
                                    }

                                    String dateRez = response.getString("releaseDate").substring(0, response.getString("releaseDate").indexOf('T'));
                                    String initDateRez = response.getString("initialReleaseDate").substring(0, response.getString("releaseDate").indexOf('T'));
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
                                    LocalDate relDate = LocalDate.parse(dateRez, formatter);
                                    LocalDate initReleaseDate = LocalDate.parse(initDateRez, formatter);

                                    for (int n = 0; n < arrayAuth.length(); n++) {
                                        JSONObject object = arrayAuth.getJSONObject(n);
                                        Author auth = new Author(Integer.parseInt(object.getString("ID")), object.getString("authorName"));
                                        authorParse.add(auth);
                                    }

                                    for (int n = 0; n < arrayGenre.length(); n++) {
                                        JSONObject object = arrayGenre.getJSONObject(n);
                                        Genre gen = new Genre(Integer.parseInt(object.getString("ID")), object.getString("genreName"));
                                        genreParse.add(gen);
                                    }

                                    Book newBook = new Book();
                                    newBook.setBookPic(response.getString("bookPic").getBytes());
                                    newBook.setID(Integer.parseInt(response.getString("ID")));
                                    newBook.setTitle(response.getString("title"));
                                    newBook.setGenre(genreParse);
                                    newBook.setAuthor(authorParse);

                                    DecimalFormat df = new DecimalFormat("#.##");
                                    double ratingCut = Double.parseDouble(response.getString("rating"));
                                    newBook.setRating(Double.parseDouble(df.format(ratingCut)));

                                    newBook.setReleaseDate(relDate);
                                    newBook.setInitialReleaseDate(initReleaseDate);
                                    newBook.setSeries(response.getString("series"));

                                    newBook.setIsbn(response.getString("isbn"));
                                    newBook.setNoPage(Integer.parseInt(response.getString("noPage")));
                                    newBook.setEdition(response.getString("edition"));
                                    newBook.setbLanguage(response.getString("bLanguage"));
                                    newBook.setPublisher(response.getString("publisher"));
                                    newBook.setPublisherSite(response.getString("publisherSite"));
                                    newBook.setBookFormat(response.getString("bookFormat"));
                                    newBook.setOverview(response.getString("overview"));

                                    byte[] x = Base64.decode(newBook.getBookPic(), Base64.DEFAULT);
                                    Bitmap bmp = BitmapFactory.decodeByteArray(x, 0, x.length);

                                    String genreForText = "";
                                    String authorForText = "";

                                    if (newBook.getGenre() != null)
                                    {
                                        for (Genre g : newBook.getGenre()) {
                                            genreForText = genreForText + g.getGenreName() + ". ";
                                        }
                                    }
                                    else
                                    {
                                        genreForText = "No known genres";
                                    }

                                    if (newBook.getAuthor() != null)
                                    {
                                        for (Author a : newBook.getAuthor()) {
                                            authorForText = authorForText + a.getAuthorName() + ". ";
                                        }
                                    }
                                    else
                                    {
                                        authorForText = "No known authors";
                                    }


                                    thumbNail.setImageBitmap(bmp);
                                    title.setText(newBook.getTitle());
                                    rating.setText(Double.toString(newBook.getRating()));
                                    genre.setText(genreForText);
                                    author.setText(authorForText);
                                    seriesBookDesc.setText(newBook.getSeries());
                                    year.setText(newBook.getReleaseDate().toString());
                                    initialYear.setText(newBook.getInitialReleaseDate().toString());
                                    isbn.setText(newBook.getIsbn());
                                    noPage.setText(Integer.toString(newBook.getNoPage()));
                                    edition.setText(newBook.getEdition());
                                    bLanguage.setText(newBook.getbLanguage());
                                    publisher.setText(newBook.getPublisher());
                                    publisherSite.setText(newBook.getPublisherSite());
                                    bookFormat.setText(newBook.getBookFormat());
                                    overview.setText(newBook.getOverview());

                                    book =newBook;
                                    getOneReview();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e("ERROR", "Error occurred ", error);
                Snackbar snackbar2 = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), error.toString(), Snackbar.LENGTH_LONG).setDuration(2000);
                snackbar2.show();


                if((descriptConstraintLayout).getChildCount() > 0) {
                    (descriptConstraintLayout).removeAllViews();
                    TextView tv0 = new TextView(getContext());
                    tv0.setText("Error. Please try again later");
                    descriptConstraintLayout.addView(tv0);
                }
            }
        })
        {
            @Override
            public Map<String, String> getHeaders()
            {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String auth_token_string = settings.getString("token", "");

                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Basic " + auth_token_string);
                return params;
            }

        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
}
