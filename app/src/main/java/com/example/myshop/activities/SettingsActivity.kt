package com.example.myshop.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.example.myshop.R
import com.example.myshop.databinding.RegBinding
import com.example.myshop.databinding.SettingsBinding
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.User
import com.example.myshop.utils.Constants
import com.example.myshop.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User


    override fun onCreate(savedInstanceState: Bundle?) {
        val binding : SettingsBinding = DataBindingUtil.setContentView(this@SettingsActivity, R.layout.activity_settings)
        super.onCreate(savedInstanceState)
        setupActionBar()
        binding.btnLogout.setOnClickListener(this)
        binding.tvEdit.setOnClickListener(this)
    }

  private fun setupActionBar(){
      var toolbarSettingsActivity =findViewById<Toolbar>(R.id.toolbar_settings_activity)
      setSupportActionBar(toolbarSettingsActivity)

      val actionBar = supportActionBar
      if(actionBar!=null){
          actionBar.setDisplayHomeAsUpEnabled(true)
          actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
      }
      toolbarSettingsActivity.setNavigationOnClickListener{onBackPressed()}
  }
    private fun getUserDetails(){
        showProgressDialog()
        FirestoreClass().getUserDetails(this@SettingsActivity)

    }

    fun userDetailsSuccess(user: User){
            mUserDetails = user
            val ivUserPhoto=findViewById<ImageView>(R.id.iv_user_photo)
            hideProgressDialog()
            GlideLoader(this@SettingsActivity).loadUserPicture(user.image,ivUserPhoto)
            findViewById<TextView>(R.id.tv_name).text = "${user.firstName} ${user.lastName}"
            findViewById<TextView>(R.id.tv_gender).text =user.gender
            findViewById<TextView>(R.id.tv_email).text =user.email
            findViewById<TextView>(R.id.tv_mobile_number).text ="${user.mobile}"


    }
    override fun onResume(){
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {
        if(v!=null){
            when(v.id){
                R.id.tv_edit ->{
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)

                }
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }

    }
}