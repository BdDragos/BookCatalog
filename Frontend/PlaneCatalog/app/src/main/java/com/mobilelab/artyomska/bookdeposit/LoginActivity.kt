package com.mobilelab.artyomska.bookdeposit

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText

import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.mobilelab.artyomska.bookdeposit.utils.CheckNetwork

import java.util.HashMap
import com.mobilelab.artyomska.bookdeposit.utils.Constants.URL

class LoginActivity : AppCompatActivity() {

    private var email: EditText? = null
    private var password: EditText? = null
    private var PERMISSION_ALL = 1
    private var PERMISSIONS = arrayOf(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val settings = PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
        val auth_token = settings.getString("token", "")

        email = findViewById(R.id.emailInput)
        password = findViewById(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerBut)
        val forgotPasswordButton = findViewById<Button>(R.id.forgotPasswordButton)

        if (CheckNetwork.isNetworkConnected(this))
        {
            loginButton.setOnClickListener { validate(email!!.text.toString(), password!!.text.toString()) }
            forgotPasswordButton.setOnClickListener { recoverPassword(email!!.text.toString()) }
        }
        else if (auth_token!!.equals("", ignoreCase = true))
        {
            val alertDialog = AlertDialog.Builder(this@LoginActivity, R.style.AppTheme_PopupOverlay).create()
            alertDialog.setTitle("Warning")
            alertDialog.setMessage("No internet connection. You won't be able to login")
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
            ) { dialog, which -> dialog.dismiss() }
            alertDialog.show()
            registerButton.isEnabled = false
            loginButton.isEnabled = false
            forgotPasswordButton.isEnabled = false
        }
        registerButton.setOnClickListener {
            val intent = Intent(baseContext, RegisterActivity::class.java)
            startActivity(intent)
        }

        checkAllPermission()

    }



    public override fun onResume() {
        super.onResume()

        val settings = PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
        val auth_token = settings.getString("token", "")
        if (auth_token != "") {
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun recoverPassword(email: String) {
        val pDialog = ProgressDialog(this)
        pDialog.setMessage("Loading...")
        pDialog.show()

        val tag_json_obj = "json_obj_req"
        val url = "$URL/userdata/RecoverPassword"
        val strReq = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
            if (response.compareTo("false") != 0) {
                pDialog.dismiss()

                val alertDialog = AlertDialog.Builder(this@LoginActivity, R.style.AppTheme_PopupOverlay).create()
                alertDialog.setTitle("Email Sent")
                alertDialog.setMessage("Check you email for the password")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()


            } else {
                pDialog.dismiss()

                val alertDialog = AlertDialog.Builder(this@LoginActivity, R.style.AppTheme_PopupOverlay).create()
                alertDialog.setTitle("Error")
                alertDialog.setMessage("No user with this email found")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }
        }, Response.ErrorListener { error ->
            Log.e("ERROR", "Error occurred ", error)
            val snackbar2 = Snackbar.make(window.decorView.rootView, "Server error", Snackbar.LENGTH_LONG).setDuration(2000)
            pDialog.dismiss()
            snackbar2.show()
        }) {

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = email


                return params
            }

        }
        strReq.retryPolicy = DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj)
    }

    private fun validate(userEmail: String, userPassword: String) {
        val pDialog = ProgressDialog(this)
        pDialog.setMessage("Loading...")
        pDialog.show()

        val tag_json_obj = "json_obj_req"
        val url = "$URL/userdata/VerifyUser"
        val strReq = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
            if (response.compareTo("false") != 0) {
                pDialog.dismiss()
                val settings = PreferenceManager
                        .getDefaultSharedPreferences(this@LoginActivity)
                val editor = settings.edit()
                editor.clear()
                editor.putString("token", response)
                editor.putBoolean("hasLoggedIn", true)
                editor.putString("userEmail", userEmail)
                editor.apply()

                val intent = Intent(baseContext, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                pDialog.dismiss()

                val alertDialog = AlertDialog.Builder(this@LoginActivity, R.style.AppTheme_PopupOverlay).create()
                alertDialog.setTitle("Can't login")
                alertDialog.setMessage("Invalid email or password")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }
        }, Response.ErrorListener { error ->
            Log.e("ERROR", "Error occurred ", error)
            val snackbar2 = Snackbar.make(window.decorView.rootView, "Server error", Snackbar.LENGTH_LONG).setDuration(2000)
            pDialog.dismiss()
            snackbar2.show()
        }) {

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["username"] = "user"
                params["email"] = userEmail
                params["userpass"] = userPassword


                return params
            }

        }
        strReq.retryPolicy = DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj)
    }


    private fun hasPermission(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun checkAllPermission() {
        if (!hasPermission(this@LoginActivity, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this@LoginActivity, PERMISSIONS, PERMISSION_ALL)
        }
    }
}


