package com.example.myshop.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.myshop.R
import com.example.myshop.databinding.ForgotPassBinding
import com.example.myshop.databinding.LogBinding
import com.example.myshop.databinding.RegBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ForgotPassBinding= DataBindingUtil.setContentView(this@ForgotPasswordActivity, R.layout.activity_forgot_password)
        setupActionBar(binding.toolbarForgotPasswordActivity)


        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }
    private fun setupActionBar(toolbar: androidx.appcompat.widget.Toolbar){

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        toolbar.setNavigationOnClickListener{onBackPressed()}

        findViewById<Button>(R.id.btn_submit).setOnClickListener{
            val email: String = findViewById<TextView>(R.id.et_email_forgot_pw).text.toString().trim{it <= ' '}
            if(email.isEmpty()){
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
            } else {
                showProgressDialog()
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener{ task ->
                    hideProgressDialog()
                    if(task.isSuccessful){
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            resources.getString(R.string.email_sent_success),
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }
                }
            }

        }

    }



}