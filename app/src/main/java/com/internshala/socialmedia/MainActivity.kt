package com.internshala.socialmedia

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.facebook.CallbackManager
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.FacebookCallback
import java.util.*
import java.util.Arrays.asList
import com.facebook.login.LoginManager
import android.content.Intent
import android.widget.Button
import com.internshala.socialmedia.R.id.login_button
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fbbttn=findViewById<Button>(R.id.fbbtn)
        val linkedin=findViewById<Button>(R.id.linkdbtn)

        linkedin.setOnClickListener {
            val intent = Intent(this, Link::class.java)
            startActivity(intent)
        }
        fbbttn.setOnClickListener {
            val intent = Intent(this, Facebook::class.java)
            startActivity(intent)

        }

    }







}

