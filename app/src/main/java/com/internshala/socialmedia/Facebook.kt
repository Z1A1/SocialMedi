package com.internshala.socialmedia


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.facebook.login.LoginManager
import java.util.*


class Facebook : AppCompatActivity() {
    private var callbackManager: CallbackManager? = null
    lateinit var facebook_login: Button
    lateinit var name: TextView
    lateinit var email: TextView
    lateinit var dob: TextView
    lateinit var gender: TextView
    lateinit var profile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facebook)
        callbackManager = CallbackManager.Factory.create()
        facebook_login = findViewById(R.id.login_button) as Button
        name = findViewById(R.id.tvName) as TextView
        email = findViewById(R.id.tvEmail) as TextView
        dob = findViewById(R.id.tvDOB) as TextView
        gender = findViewById(R.id.tvGender) as TextView
        profile = findViewById(R.id.ivProfile) as ImageView

        facebook_login.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
        }
        fblogin()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)

    }

    fun fblogin() {
        LoginManager.getInstance().registerCallback(callbackManager!!, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val accessToken = AccessToken.getCurrentAccessToken()
                val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->
                    try {
                        if (`object`.has("name")) {
                            name.setText(`object`.getString("name"))
                        }
                        if (`object`.has("email")) {
                            email.setText(`object`.getString("email"))
                        }
                        if (`object`.has("gender")) {
                            gender.setText(`object`.getString("gender"))
                        }
                        if (`object`.has("picture")) {
                            Glide.with(this@Facebook)
                                    .load(`object`.getJSONObject("picture").getJSONObject("data").getString("url"))
                                    .into(profile)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)")
                request.parameters = parameters
                request.executeAsync()

            }

            override fun onCancel() {
                //TODO Auto-generated method stub
                Toast.makeText(this@Facebook, "Cancel", Toast.LENGTH_LONG).show()

            }

            override fun onError(error: FacebookException) {
                //TODO Auto-generated method stub
                Toast.makeText(this@Facebook, "onError", Toast.LENGTH_LONG).show()

            }
        })

    }





}
