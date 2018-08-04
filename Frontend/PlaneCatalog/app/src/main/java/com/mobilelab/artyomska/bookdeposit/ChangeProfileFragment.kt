package com.mobilelab.artyomska.bookdeposit

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.mobilelab.artyomska.bookdeposit.utils.Constants
import com.mobilelab.artyomska.bookdeposit.utils.ValidateEmail
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.regex.Pattern

class ChangeProfileFragment : Fragment() {

    private var buttonChange: Button? = null
    private var deleteAccount : Button? = null
    private var newUsernameChange : EditText? = null
    private var newPasswordChange : EditText? = null
    private var newEmailChange : EditText? = null
    private var oldPasswordChange : EditText? = null
    private var newOverviewChange : EditText? = null
    private var userID : String? = null
    private var repeatPassword : EditText? = null
    private var newUserPic : ImageView? = null
    private val RESULT_LOAD_IMAGE = 1
    private var activityMain : MainActivity? = null
    private var mContext : Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        activityMain = activity as MainActivity?
        val RootView = inflater.inflate(R.layout.change_profile_data, container, false)


        oldPasswordChange = RootView.findViewById( R.id.oldPasswordChange )
        newUsernameChange = RootView.findViewById( R.id.newUsernameChange )
        newPasswordChange = RootView.findViewById( R.id.newPasswordChange )
        newEmailChange = RootView.findViewById( R.id.newEmailChange )
        newOverviewChange = RootView.findViewById( R.id.newOverviewChange )
        buttonChange = RootView.findViewById( R.id.buttonChange )
        deleteAccount = RootView.findViewById( R.id.deleteAccount )
        repeatPassword = RootView.findViewById( R.id.repeatPassword )
        newUserPic = RootView.findViewById( R.id.newUserPic )

        userID = arguments?.getString("userID")

        buttonChange!!.setOnClickListener {
            changeUserData(userID)
        }

        deleteAccount!!.setOnClickListener {
            deleteUserData(userID)
        }

        newUserPic!!.setOnClickListener {
            val i = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            startActivityForResult(i, RESULT_LOAD_IMAGE)
        }


        return RootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {

                val imageUri = resultData.data
                newUserPic?.setImageURI(imageUri);
            }
        }
    }

    private fun deleteUserData(userID: String?) {

        val alertDialog = mContext?.let { AlertDialog.Builder(it, R.style.AppTheme_PopupOverlay).create() }
        alertDialog?.setTitle("Warning")
        alertDialog?.setMessage("Please confirm your account deletion")

        val oldPass : String? = oldPasswordChange?.text.toString();

        if (oldPass.equals(""))
        {
            val snackbar2 = Snackbar.make(activityMain!!.window.decorView.rootView, "Introduce your actual password", Snackbar.LENGTH_LONG).setDuration(2000)
            snackbar2.show()
        }
        else
        {
            alertDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog, which ->

                dialog.dismiss()

                val pDialog = ProgressDialog(activity)
                pDialog.setMessage("Deleting the user...")
                pDialog.show()

                val tag_json_obj = "json_obj_req"
                val url = "${Constants.URL}/userdata/DeleteUserData"
                val strReq = object : StringRequest(Request.Method.POST, url,
                        Response.Listener { response ->
                            pDialog.dismiss()
                            if (response.equals("true"))
                            {
                                val snackbar2 = Snackbar.make(activityMain!!.window.decorView.rootView, "The user was deleted", Snackbar.LENGTH_LONG).setDuration(2000)
                                snackbar2.show()

                                val settings = PreferenceManager.getDefaultSharedPreferences(activityMain)
                                val editor = settings.edit()
                                editor.clear()
                                editor.apply()

                                val intent = Intent(context, LoginActivity::class.java)
                                startActivity(intent)

                                activityMain!!.finish()
                            }
                            else
                            {
                                val snackbar2 = Snackbar.make(activityMain!!.window.decorView.rootView, "The user was not deleted", Snackbar.LENGTH_LONG).setDuration(2000)
                                snackbar2.show()
                            }
                        }, Response.ErrorListener { error ->
                    pDialog.dismiss()
                    Log.e("ERROR", "Error occurred ", error)
                    val snackbar2 = Snackbar.make(activityMain!!.window.decorView.rootView, "Server error. User wasn't deleted", Snackbar.LENGTH_LONG).setDuration(2000)
                    snackbar2.show()
                })
                {
                    override fun getHeaders(): Map<String, String> {
                        val settings = PreferenceManager.getDefaultSharedPreferences(activityMain)
                        val auth_token_string = settings.getString("token", "")

                        val params = HashMap<String, String>()
                        params["Authorization"] = "Basic " + auth_token_string!!
                        return params
                    }

                    override fun getParams(): HashMap<String?, String?> {
                        val params = HashMap<String?, String?>()
                        params["ID"] = userID
                        params["userpass"] = oldPass

                        return params
                    }
                }

                strReq.retryPolicy = DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                AppController.getInstance().addToRequestQueue(strReq, tag_json_obj)


            }
            alertDialog?.setButton(AlertDialog.BUTTON_NEGATIVE, "NO") { dialog, which ->

                dialog.dismiss()
            }
            alertDialog?.show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

    private fun changeUserData(id : String?) {

        val pDialog = ProgressDialog(activity)
        pDialog.setMessage("Changing the data...")
        pDialog.show()

        val oldPass : String? = oldPasswordChange?.text.toString();
        val newUser : String? = newUsernameChange?.text.toString();
        val newPass : String? = newPasswordChange?.text.toString();
        val newEmail : String? = newEmailChange?.text.toString();
        val newOver : String? = newOverviewChange?.text.toString();
        val newRepeatPass : String? = repeatPassword?.text.toString();


        if (!newEmail.equals("") && !ValidateEmail.isValidEmailAddress(newEmail))
        {
            val snackbar2 = Snackbar.make(activityMain!!.window.decorView.rootView, "The email address is invalid", Snackbar.LENGTH_LONG).setDuration(2000)
            snackbar2.show()
            pDialog.dismiss()
        }
        else if (oldPass.equals(""))
        {
            val snackbar2 = Snackbar.make(activityMain!!.window.decorView.rootView, "Introduce your current password", Snackbar.LENGTH_LONG).setDuration(2000)
            snackbar2.show()
            pDialog.dismiss()
        }
        else  if (newPass.equals(newRepeatPass))
        {
            val imageView = newUserPic
            var byteIn64 = ""
            if(imageView?.drawable !is AdaptiveIconDrawable)
            {
                val bitmap = (imageView?.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val b = baos.toByteArray()
                byteIn64 = Base64.encodeToString(b, Base64.DEFAULT)
            }

            val tag_json_obj = "json_obj_req"
            val url = "${Constants.URL}/userdata/ChangeUserData"
            val strReq = object : StringRequest(Request.Method.POST, url,
                    Response.Listener { response ->
                        pDialog.dismiss()
                        if (response.equals("invalid"))
                        {
                            val snackbar2 = Snackbar.make(activityMain!!.window.decorView.rootView, "The old password is invalid", Snackbar.LENGTH_LONG).setDuration(2000)
                            snackbar2.show()
                        }
                        else if (response.equals("true"))
                        {
                            val snackbar2 = Snackbar.make(activityMain!!.window.decorView.rootView, "The data was changed with success", Snackbar.LENGTH_LONG).setDuration(2000)
                            snackbar2.show()

                            if (!newEmail.equals("") && !newEmail.equals(null) && !newEmail.equals("null"))
                            {
                                val settings = PreferenceManager
                                        .getDefaultSharedPreferences(activityMain)
                                val editor = settings.edit()
                                editor.remove("userEmail")
                                editor.putString("userEmail", newEmail)
                                editor.apply()
                            }

                            activityMain?.recreate();
                        }
                    }, Response.ErrorListener { error ->
                pDialog.dismiss()
                Log.e("ERROR", "Error occurred ", error)
                val snackbar2 = Snackbar.make(activityMain!!.window.decorView.rootView, "Server error", Snackbar.LENGTH_LONG).setDuration(2000)
                snackbar2.show()
            })
            {
                override fun getHeaders(): Map<String, String> {
                    val settings = PreferenceManager.getDefaultSharedPreferences(activityMain)
                    val auth_token_string = settings.getString("token", "")

                    val params = HashMap<String, String>()
                    params["Authorization"] = "Basic " + auth_token_string!!
                    return params
                }

                override fun getParams(): HashMap<String?, String?> {
                    val params = HashMap<String?, String?>()
                    params["ID"] = id
                    params["username"] = newUser
                    params["userpass"] = oldPass
                    params["newPass"] = newPass
                    params["email"] = newEmail
                    params["userPic"] = byteIn64
                    params["overview"] = newOver

                    return params
                }
            }

            strReq.retryPolicy = DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            AppController.getInstance().addToRequestQueue(strReq, tag_json_obj)
        }
        else
        {
            pDialog.dismiss()
            val snackbar2 = Snackbar.make(activityMain!!.window.decorView.rootView, "The new passwords aren't the same. Please reinput them", Snackbar.LENGTH_LONG).setDuration(2000)
            snackbar2.show()

        }

    }
}