package com.internshala.socialmedia

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.linkedin.platform.APIHelper
import com.linkedin.platform.LISessionManager
import com.linkedin.platform.errors.LIApiError
import com.linkedin.platform.errors.LIAuthError
import com.linkedin.platform.listeners.ApiListener
import com.linkedin.platform.listeners.ApiResponse
import com.linkedin.platform.listeners.AuthListener
import com.linkedin.platform.utils.Scope
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_link.*
import org.json.JSONException
import java.security.MessageDigest

class Link : AppCompatActivity(), View.OnClickListener {

    private var imgProfile: ImageView? = null
    private var imgLogin:ImageView? = null
    private var txtDetails: TextView? = null
    private var btnLogout: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link)
        computePakageHash()
        initializeControls()
    }

    private fun initializeControls() {
        val imgLogin = findViewById(R.id.imgLogin) as ImageView
        imgLogin!!.setOnClickListener(this)
        btnLogout = findViewById(R.id.btnLogout) as Button
        btnLogout!!.setOnClickListener(this)
        imgProfile = findViewById(R.id.imgProfile) as ImageView
        txtDetails = findViewById(R.id.txtDetails) as TextView

        //Default
        imgLogin!!.setVisibility(View.VISIBLE)
        btnLogout!!.visibility = View.GONE
        imgProfile!!.visibility = View.GONE
        txtDetails!!.visibility = View.GONE
    }

    private fun computePakageHash() {
        try {
            val info = packageManager.getPackageInfo(
                    "com.supportmania.inlogin",
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: Exception) {
            Log.e("TAG", e.message)
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgLogin -> handleLogin()
            R.id.btnLogout -> handleLogout()
        }
    }

    private fun handleLogout() {
        LISessionManager.getInstance(applicationContext).clearSession()
        imgLogin!!.setVisibility(View.VISIBLE)
        btnLogout!!.visibility = View.GONE
        imgProfile!!.visibility = View.GONE
        txtDetails!!.visibility = View.GONE
    }

    private fun handleLogin() {
        LISessionManager.getInstance(applicationContext).init(this, buildScope(), object : AuthListener {
            override fun onAuthSuccess() {
                // Authentication was successful.  You can now do
                // other calls with the SDK.
                imgLogin!!.setVisibility(View.GONE)
                btnLogout!!.visibility = View.VISIBLE
                imgProfile!!.visibility = View.VISIBLE
                txtDetails!!.visibility = View.VISIBLE
                fetchPersonalInfo()
            }

            override fun onAuthError(error: LIAuthError) {
                // Handle authentication errors
                Log.e("NIKHIL", error.toString())
            }
        }, true)
    }

    // Build the list of member permissions our LinkedIn session requires
    private fun buildScope(): Scope {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // Add this line to your existing onActivityResult() method
        LISessionManager.getInstance(applicationContext).onActivityResult(this, requestCode, resultCode, data)
    }

    private fun fetchPersonalInfo() {
        val url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,public-profile-url,picture-url,email-address,picture-urls::(original))"

        val apiHelper = APIHelper.getInstance(applicationContext)
        apiHelper.getRequest(this, url, object : ApiListener {
            override fun onApiSuccess(apiResponse: ApiResponse) {
                // Success!
                try {
                    val jsonObject = apiResponse.responseDataAsJson
                    val firstName = jsonObject!!.getString("firstName")
                    val lastName = jsonObject.getString("lastName")
                    val pictureUrl = jsonObject.getString("pictureUrl")
                    val emailAddress = jsonObject.getString("emailAddress")

                    Picasso.with(applicationContext).load(pictureUrl).into(imgProfile)

                    val sb = StringBuilder()
                    sb.append("First Name: $firstName")
                    sb.append("\n\n")
                    sb.append("Last Name: $lastName")
                    sb.append("\n\n")
                    sb.append("Email: $emailAddress")
                    txtDetails!!.text = sb
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onApiError(liApiError: LIApiError) {
                // Error making GET request!

            }
        })
    }
}