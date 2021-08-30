package com.example.myshop.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.myshop.R
import com.example.myshop.utils.Constants

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val sharePreferences = getSharedPreferences(Constants.MYSHOP_PREFERENCES, Context.MODE_PRIVATE)
        val userName = sharePreferences.getString(Constants.LOGGED_IN_USERNAME,"")
        findViewById<TextView>(R.id.tv_main).text = "The logged in user is $userName."




    }
}