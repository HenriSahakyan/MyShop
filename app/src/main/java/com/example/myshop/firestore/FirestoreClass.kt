package com.example.myshop.firestore

 import android.app.Activity
 import android.content.Context
 import android.content.SharedPreferences
 import android.media.session.MediaSessionManager
 import android.net.Uri
 import android.util.Log
 import com.example.myshop.activities.LoginActivity
 import com.example.myshop.activities.RegisterActivity
 import com.example.myshop.activities.SettingsActivity
 import com.example.myshop.activities.UserProfileActivity
 import com.example.myshop.models.User
 import com.example.myshop.utils.Constants
 import com.google.firebase.auth.FirebaseAuth
 import com.google.firebase.firestore.FirebaseFirestore
 import com.google.firebase.firestore.SetOptions
 import com.google.firebase.storage.FirebaseStorage
 import com.google.firebase.storage.StorageReference


class FirestoreClass {

 private val mFireStore = FirebaseFirestore.getInstance()

 fun registerUser(activity: RegisterActivity, userInfo: User) {

  mFireStore.collection(Constants.USERS)
   .document(userInfo.id)
   .set(userInfo, SetOptions.merge())
   .addOnSuccessListener {

    activity.userRegistrationSuccess()

   }
   .addOnFailureListener { e ->
    activity.hideProgressDialog()
    Log.e(
     activity.javaClass.simpleName,
     "Error while registering the user",
     e
    )
   }
 }

 private fun getCurrentUserID(): String {

  val currentUser = FirebaseAuth.getInstance().currentUser

  var currentUserID = ""
  if (currentUser != null) {
   currentUserID = currentUser.uid
  }

  return currentUserID

 }

 fun getUserDetails(activity: Activity) {

  mFireStore.collection(Constants.USERS)
   .document(getCurrentUserID())
   .get()
   .addOnSuccessListener { document ->

    Log.i(activity.javaClass.simpleName, document.toString())

    val user = document.toObject(User::class.java)!!

    val sharedPreferences =
     activity.getSharedPreferences(Constants.MYSHOP_PREFERENCES, Context.MODE_PRIVATE)

    val editor: SharedPreferences.Editor = sharedPreferences.edit()

    editor.putString(
     Constants.LOGGED_IN_USERNAME, "${user.firstName} ${user.lastName}"
    )

    editor.apply()

    when (activity) {

     is LoginActivity -> {

      activity.userLoggedInSuccess(user)

     }
     is SettingsActivity -> {
      activity.userDetailsSuccess(user)
     }

    }

   }
   .addOnFailureListener { e ->
    when (activity) {
     is LoginActivity -> {
      activity.hideProgressDialog()
     }
     is SettingsActivity ->{
      activity.hideProgressDialog()
     }
    }
    Log.e(

     activity.javaClass.simpleName,
     "Error 404",
     e
    )

   }

 }

 fun updateUserProfileData(activity: Activity,userHashMap: HashMap<String,Any>){

   mFireStore.collection(Constants.USERS)
    .document(getCurrentUserID())
    .update(userHashMap)
    .addOnSuccessListener {
     when (activity){
      is UserProfileActivity -> {
       activity.userProfileUpdateSuccess()
      }
     }

    }
    .addOnFailureListener{ e ->
      when (activity){
       is UserProfileActivity -> {
        activity.hideProgressDialog()
       }
      }
     Log.e(
      activity.javaClass.simpleName,
      "Error while updating the user details. ",
      e
     )
    }
 }

 fun uploadImageToCloudStorage(activity: Activity,imageFileUri: Uri?){

   val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
    Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." +
            Constants.getFileExtension(activity,imageFileUri))

  sRef.putFile(imageFileUri!!).addOnSuccessListener { taskSnapshot ->

   Log.e(
    "Firebase Image URL",
    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
   )
   taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
    Log.e("Downloadable Image URL ", uri.toString())
    when(activity){
     is UserProfileActivity -> {
      activity.imageUploadSuccess(uri.toString())
     }
    }

   }

  }
   .addOnFailureListener{ exception ->

    when(activity){
     is UserProfileActivity -> {
      activity.hideProgressDialog()
     }
    }
    Log.e(
     activity.javaClass.simpleName,
     exception.message,
     exception
    )




   }

 }


}