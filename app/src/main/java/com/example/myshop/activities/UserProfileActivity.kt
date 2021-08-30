package com.example.myshop.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.myshop.R
import com.example.myshop.databinding.UserProfBinding
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.User
import com.example.myshop.utils.Constants
import com.example.myshop.utils.GlideLoader
import java.io.IOException


class UserProfileActivity() : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? =null
    private var mUserProfileImageURL:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: UserProfBinding =
            DataBindingUtil.setContentView(this@UserProfileActivity, R.layout.activity_user_profile)


        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }





        if (mUserDetails.profileCompleted == 0) {
            binding.tvTitle.text = resources.getString(R.string.title_complete_profile)
            binding.etFirstName.isEnabled = false
            binding.etFirstName.setText(mUserDetails.firstName)
            binding.etLastName.isEnabled = false
            binding.etLastName.setText(mUserDetails.lastName)
            binding.etEmail.isEnabled=false
            binding.etEmail.setText(mUserDetails.email)


        } else {
            binding.tvTitle.text = resources.getString(R.string.title_edit_profile)
            GlideLoader(this@UserProfileActivity).loadUserPicture(
                mUserDetails.image,
                binding.ivUserPhoto
            )
            binding.etFirstName.setText(mUserDetails.firstName)
            binding.etLastName.setText(mUserDetails.lastName)

            binding.etEmail.isEnabled=false
            binding.etEmail.setText(mUserDetails.email)

            if(mUserDetails.mobile != 0L){
                binding.etMobileNumber.setText(mUserDetails.mobile.toString())
            }
            if(mUserDetails.gender == Constants.MALE){
                binding.rbMale.isChecked=true
            } else {
                binding.rbFemale.isChecked=true
            }
        }

            binding.ivUserPhoto.setOnClickListener(this@UserProfileActivity)
            binding.btnSave.setOnClickListener(this@UserProfileActivity)


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
    override fun onClick(v: View?){
        if (v!=null) {
            when (v.id) {

                R.id.iv_user_photo -> {

                    if(ContextCompat.checkSelfPermission(
                            this,
                           android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )== PackageManager.PERMISSION_GRANTED) {
                        showErrorSnackBar("You already have the storage permission. ",false)
                        Constants.showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE

                        )
                    }
                }

                R.id.btn_save -> {


                    if(validateUserProfileDetails()){
                        showProgressDialog()
                        if(mSelectedImageFileUri!=null) {
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri)
                        }else{
                            updateUserProfileDetails()
                        }

                        //showErrorSnackBar("Your details are valid. You can update them.",false)
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails(){
        val userHashMap = HashMap<String, Any>()
        val firstName =  findViewById<TextView>(R.id.et_first_name).text.toString().trim{it<=' '}
        if(firstName != mUserDetails.firstName){
            userHashMap[Constants.FIRST_NAME]
        }
        val lastName = findViewById<TextView>(R.id.et_last_name).text.toString().trim{it <= ' '}
        if(lastName != mUserDetails.lastName){
            userHashMap[Constants.LAST_NAME]
        }

        val mobileNumber= findViewById<TextView>(R.id.et_mobile_number).text.toString().trim{it <= ' '}
        val gender = if(findViewById<RadioButton>(R.id.rb_male).isChecked){
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if(mUserProfileImageURL.isNotEmpty()){
            userHashMap[Constants.IMAGE]=mUserProfileImageURL
        }

        if(mobileNumber.isNotEmpty()&& mobileNumber!= mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE]=mobileNumber.toLong()
        }
        if(gender.isNotEmpty() && gender != mUserDetails.gender){
            userHashMap[Constants.GENDER]=gender
        }
        userHashMap[Constants.GENDER]=gender
        userHashMap[Constants.COMPLETE_PROFILE]= 1

        FirestoreClass().updateUserProfileData(this,userHashMap)

    }

   fun userProfileUpdateSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

       startActivity(Intent(this@UserProfileActivity,DashboardActivity::class.java))
       finish()

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
             } else {
                 Toast.makeText(
                     this,
                     resources.getString(R.string.read_storage_permission_denied),
                     Toast.LENGTH_LONG).show()

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if(data != null){
                    try{
                         mSelectedImageFileUri = data.data!!
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,findViewById<ImageView>(R.id.iv_user_photo))
                    } catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }
        } else if (resultCode ==Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }
    private fun validateUserProfileDetails():Boolean{
        return when {
            TextUtils.isEmpty(findViewById<TextView>(R.id.et_mobile_number).text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number),false)
                false
            } else -> {
                true
            }

        }
    }

    fun imageUploadSuccess (imageURL: String){
        mUserProfileImageURL =  imageURL
        updateUserProfileDetails()
    }

}