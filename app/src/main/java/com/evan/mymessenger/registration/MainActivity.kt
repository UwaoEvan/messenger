package com.evan.mymessenger.registration

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.evan.mymessenger.R
import com.evan.mymessenger.messages.DashboardActivity
import com.evan.mymessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registerBtn = button_register
        val login = sigin_text

        login.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
        registerBtn.setOnClickListener {
            performRegister()
    }
        photo_holder.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
}
    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null ){
            selectedPhotoUri = data?.data
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            profile_image_signUp.setImageBitmap(bitmap)
            photo_holder.alpha = 0f
        }
    }


    private fun imageUpload(){
        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("MainActivity", "File stored successfully ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("MainActivity", "Link $it")
                    saveUserToDb(it.toString())
                }
            }
    }

    private fun saveUserToDb(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/${uid}")

        val user = User(uid, username_register.text.toString().replaceFirstChar{it.uppercase()},profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("MainActivity", "Finally saved the user to database")
            }
    }

    private fun performRegister(){
        val email = email_register.text.toString()
        val password = password_register.text.toString()
        if(email.isNullOrEmpty() || password.isNullOrEmpty()){
            Toast.makeText(this, "Invalid Email / Password", Toast.LENGTH_SHORT).show()
            return
        }
    //            Login the user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("MainActivity", "Success ${it.result?.user?.uid}")
                    imageUpload()
                    var intent = Intent(this, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK. or (Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener{
                Log.d("MainActivity", "Failed because of ${it.message}")
                return@addOnFailureListener
            }
        }
    }

