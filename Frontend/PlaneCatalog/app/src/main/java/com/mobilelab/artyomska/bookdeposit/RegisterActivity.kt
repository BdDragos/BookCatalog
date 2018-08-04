package com.mobilelab.artyomska.bookdeposit

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView

import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.mobilelab.artyomska.bookdeposit.utils.CheckNetwork

import java.io.ByteArrayOutputStream
import java.util.HashMap

import com.mobilelab.artyomska.bookdeposit.utils.Constants.URL
import com.mobilelab.artyomska.bookdeposit.utils.ValidateEmail
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private var userText: EditText? = null
    private var passText: EditText? = null
    private var emailText: EditText? = null
    private var repeatPassText: EditText? = null
    private var registerBut: Button? = null
    private var clearBut: Button? = null
    private var image: ImageView? = null
    private val bbytes: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userText = findViewById(R.id.userText)
        passText = findViewById(R.id.passText)
        repeatPassText = findViewById(R.id.repeatPassText)
        registerBut = findViewById(R.id.registerBut)
        clearBut = findViewById(R.id.clearBut)
        emailText = findViewById(R.id.emailText)
        image = findViewById(R.id.registerPic)

        registerBut!!.setOnClickListener { register(userText!!.text.toString(), passText!!.text.toString(), repeatPassText!!.text.toString(), emailText!!.text.toString()) }

        image!!.setOnClickListener {
            val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, RESULT_LOAD_IMAGE)
        }

        clearBut!!.setOnClickListener {
            userText!!.setText("")
            passText!!.setText("")
            repeatPassText!!.setText("")
            emailText!!.setText("")
            image!!.setImageResource(android.R.color.transparent);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {

                val imageUri = resultData.data
                image!!.setImageURI(imageUri)

            }
        }
    }

    private fun register(userName: String?, userPassword: String?, repeatUserPassword: String, userEmail: String?) {


        if (userName == null || userPassword == null || userName.length < 4 || userPassword.length < 4 || userEmail == null)
        {
            val alertDialog = AlertDialog.Builder(this@RegisterActivity, R.style.AppTheme_PopupOverlay).create()
            alertDialog.setTitle("Alert")
            alertDialog.setMessage("Username,email or password are empty/length is less than 4")
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
            ) { dialog, which -> dialog.dismiss() }
            alertDialog.show()
        }
        else if (!ValidateEmail.isValidEmailAddress(userEmail))
        {
            val alertDialog = AlertDialog.Builder(this@RegisterActivity, R.style.AppTheme_PopupOverlay).create()
            alertDialog.setTitle("Alert")
            alertDialog.setMessage("Email address is invalid")
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
            ) { dialog, which -> dialog.dismiss() }
            alertDialog.show()
        }

        else if (userPassword.compareTo(repeatUserPassword) != 0)
        {
            val alertDialog = AlertDialog.Builder(this@RegisterActivity, R.style.AppTheme_PopupOverlay).create()
            alertDialog.setTitle("Alert")
            alertDialog.setMessage("Passwords aren't the same.Please retype them")
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
            ) { dialog, which -> dialog.dismiss() }
            alertDialog.show()
        } else {
            if (CheckNetwork.isNetworkConnected(this)) {
                val pDialog = ProgressDialog(this)
                pDialog.setMessage("Loading...")
                pDialog.show()

                val url = "$URL/userdata/AddUser"
                val tag_json_obj = "json_obj_req"

                val strReq = object : StringRequest(Request.Method.POST, url, Response.Listener { response ->
                    if (response.compareTo("true") == 0) {
                        pDialog.dismiss()
                        val alertDialog = AlertDialog.Builder(this@RegisterActivity, R.style.AppTheme_PopupOverlay).create()
                        alertDialog.setTitle("Success")
                        alertDialog.setMessage("User was registered")
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                        ) { dialog, which ->
                            dialog.dismiss()
                            finish()
                        }
                        alertDialog.show()
                    } else {
                        pDialog.dismiss()
                        val alertDialog = AlertDialog.Builder(this@RegisterActivity, R.style.AppTheme_PopupOverlay).create()
                        alertDialog.setTitle("Can't register")
                        alertDialog.setMessage("User already exists")
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                        ) { dialog, which -> dialog.dismiss() }
                        alertDialog.show()
                    }
                    pDialog.dismiss()
                }, Response.ErrorListener { error ->
                    Log.e("ERROR", "Error occurred ", error)
                    val snackbar2 = Snackbar.make(window.decorView.rootView, "Server error", Snackbar.LENGTH_LONG).setDuration(2000)
                    snackbar2.show()
                    pDialog.dismiss()
                }) {
                    override fun getParams(): Map<String, String> {

                        val imageView = findViewById<ImageView>(R.id.registerPic)
                        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val b = baos.toByteArray()
                        val byteIn64 = Base64.encodeToString(b, Base64.DEFAULT)

                        val params = HashMap<String, String>()
                        params["username"] = userName
                        params["userpass"] = userPassword
                        params["email"] = userEmail
                        params["userPic"] = byteIn64
                        params["permissionLvl"] = "user"

                        return params
                    }
                }
                strReq.retryPolicy = DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                AppController.getInstance().addToRequestQueue(strReq, tag_json_obj)
            } else {
                val alertDialog = AlertDialog.Builder(this@RegisterActivity, R.style.AppTheme_PopupOverlay).create()
                alertDialog.setTitle("Warning")
                alertDialog.setMessage("Cannot register. No internet connection")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }
        }
    }

    companion object {
        private val RESULT_LOAD_IMAGE = 2
    }
}
