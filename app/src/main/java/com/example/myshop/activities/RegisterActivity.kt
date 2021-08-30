package com.example.myshop.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import android.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.example.myshop.R
import com.example.myshop.databinding.RegBinding
import com.example.myshop.firestore.FirestoreClass
import com.example.myshop.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : BaseActivity()  {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


      val binding : RegBinding = DataBindingUtil.setContentView(this@RegisterActivity, R.layout.activity_register)
        binding.tvLogin.setOnClickListener {
           onBackPressed()

        }

        setupActionBar(binding.toolbarRegisterActivity)
        binding.btnRegister.setOnClickListener{
            registerUser(
                binding.etFirstName,
                binding.etLastName,
                binding.etEmail,
                binding.etPassword,
                binding.etConfirmPassword,
                binding.cbTermsAndCondition
            )
        }





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
    }


    private fun validateRegisterDetails(
        firstName: com.example.myshop.utils.MSEditText,
        lastName: com.example.myshop.utils.MSEditText,
        email: com.example.myshop.utils.MSEditText,
        password: com.example.myshop.utils.MSEditText,
        confirmPassword:com.example.myshop.utils.MSEditText,
        termAndCondition: androidx.appcompat.widget.AppCompatCheckBox):Boolean{
        return when{
            TextUtils.isEmpty(firstName.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name),true)
                false
            }
            TextUtils.isEmpty(lastName.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name),true)
                false
            }
            TextUtils.isEmpty(email.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(password.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }
            TextUtils.isEmpty(confirmPassword.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password),true)
                false
            }
            password.text.toString().trim{it<=' '} != confirmPassword.text.toString().trim{it<=' '} ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password_mismatch),true)
                false
            }
            !(termAndCondition).isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_conditions),true)
                false
            }
            else ->{

                true
            }


        }

    }

    private fun registerUser(
        etFirstName: com.example.myshop.utils.MSEditText,
        etLastName: com.example.myshop.utils.MSEditText,
        etEmail: com.example.myshop.utils.MSEditText,
        etPassword: com.example.myshop.utils.MSEditText,
        etConfirmPassword:com.example.myshop.utils.MSEditText,
        etTermAndCondition: androidx.appcompat.widget.AppCompatCheckBox){

        if( validateRegisterDetails( etFirstName, etLastName, etEmail, etPassword, etConfirmPassword, etTermAndCondition)){
            showProgressDialog()
            val email: String = etEmail.text.toString().trim{it <= ' '}
            val password: String = etPassword.text.toString().trim{it <= ' '}

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(

                OnCompleteListener <AuthResult>{ task ->

                    if(task.isSuccessful){
                        val firebaseUser: FirebaseUser = task.result!!.user!!

                        val user = User(

                            firebaseUser.uid,
                            etFirstName.text.toString().trim{it <= ' '},
                            etLastName.text.toString().trim{it <= ' '},
                            etEmail.text.toString().trim{it <= ' '}

                        )

                        FirestoreClass().registerUser(this@RegisterActivity, user)
                        FirebaseAuth.getInstance().signOut()
                        finish()
                    }
                    else{
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }

                }
            )
        }
    }
    fun userRegistrationSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@RegisterActivity, resources.getString(R.string.register_success), Toast.LENGTH_LONG).show()
    }

}