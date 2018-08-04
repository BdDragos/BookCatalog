package com.mobilelab.artyomska.bookdeposit;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mobilelab.artyomska.bookdeposit.model.UserData;
import com.mobilelab.artyomska.bookdeposit.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SearchView searchView;
    private NavigationView navigationView;
    private UserData userLogged;
    private SearchManager searchManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = findViewById(R.id.tabs);
        replaceFragment(new MainActivityTab1());

        this.setTitle("BookDeposit");

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    replaceFragment(new MainActivityTab1());
                } else if (tab.getPosition() == 1) {
                    replaceFragment(new MainActivityTab2());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    replaceFragment(new MainActivityTab1());
                } else if (tab.getPosition() == 1) {
                    replaceFragment(new MainActivityTab2());
                }
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getUserID();

    }

    private void replaceFragment(Fragment fragment) {

        if (userLogged != null) {
            Bundle bundle = new Bundle();
            bundle.putString("userID", Integer.toString(userLogged.getID()));
            fragment.setArguments(bundle);
        }

        Bundle bundle = new Bundle();
        bundle.putString("userID", Integer.toString(getLoggedUserId()));
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);

        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if(searchView.getWidth()>0)
                {
                    SearchFragment myFrag = new SearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("searchQuery", query);
                    myFrag.setArguments(bundle);


                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, myFrag);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    return true;
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(final String query)
            {
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.putBoolean("hasLoggedIn", false);
            editor.apply();
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String stredittext = data.getStringExtra("rez");
                if (stredittext.equals("1"))
                {
                    recreate();
                }
            }
        }
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Log.d("MainActivity", "Cancelled");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();

            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + intentResult.getContents(), Toast.LENGTH_LONG).show();

                BookDescriptionFragment myFrag = new BookDescriptionFragment();
                Bundle bundle = new Bundle();
                bundle.putString("isbn", intentResult.getContents());
                bundle.putString("ID", null);
                bundle.putString("userID",Integer.toString(userLogged.getID()));
                myFrag.setArguments(bundle);


                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, myFrag);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!searchView.isIconified()) {
            searchView.setIconified(true);
            recreate();
        } else {
            super.onBackPressed();
        }

    }

    public int getLoggedUserId() {
        if (userLogged != null) {
            return userLogged.getID();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile_view)
        {
            replaceFragment(new UserProfileFragment());
        }
        else if (id == R.id.settings_view)
        {
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Scan Code");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
        }
        else if (id == R.id.logout_view) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.putBoolean("hasLoggedIn", false);
            editor.apply();
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void getUserID()
    {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String userEmail = settings.getString("userEmail", "");

        Map<String, String> params = new HashMap<>();
        params.put("email", userEmail);

        String tag_json_obj = "json_obj_req";
        String url = Constants.URL + "/userdata/GetAccount";
        JsonObjectRequest strReq = new JsonObjectRequest (url, new JSONObject(params),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        pDialog.dismiss();
                        try {
                            UserData loggedInUser = new UserData(Integer.parseInt(response.getString("ID")),response.getString("username"),response.getString("email"),response.getString("userPic").getBytes());
                            String email = loggedInUser.getEmail();
                            String username = loggedInUser.getUsername();
                            View headerView = navigationView.getHeaderView(0);
                            TextView navUsername = headerView.findViewById(R.id.usernameDrawer);
                            navUsername.setText(username);
                            TextView navEmail = headerView.findViewById(R.id.emailDrawer);
                            navEmail.setText(email);
                            byte[] x = Base64.decode(loggedInUser.getUserPic(), Base64.DEFAULT);
                            Bitmap bmp= BitmapFactory.decodeByteArray(x,0,x.length);

                            ImageView navImage = headerView.findViewById(R.id.userPicDrawer);
                            navImage.setImageBitmap(bmp);

                            userLogged = loggedInUser;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error)
            {
                pDialog.dismiss();
                Log.e("ERROR", "Error occurred ", error);
                Snackbar snackbar2 = Snackbar.make(getWindow().getDecorView().getRootView(), "Server error", Snackbar.LENGTH_LONG).setDuration(2000);
                snackbar2.show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders()
            {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
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

    public void changeFragment(String id, String isbn, int i)
    {

        if (i == 3)
        {
            UserProfileFragment myFrag = new UserProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putString("ID", id);
            myFrag.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, myFrag);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else {
            BookDescriptionFragment myFrag = new BookDescriptionFragment();
            Bundle bundle = new Bundle();
            if (isbn.equals("0")) {
                isbn = null;
            }
            bundle.putString("isbn", isbn);
            bundle.putString("ID", id);
            bundle.putString("userID", Integer.toString(userLogged.getID()));
            myFrag.setArguments(bundle);


            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, myFrag);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
