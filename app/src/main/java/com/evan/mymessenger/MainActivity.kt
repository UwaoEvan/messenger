package com.evan.mymessenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null ){
            var uri = data?.data
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            profile_image_signUp.setImageBitmap(bitmap)
            photo_holder.alpha = 0f
        }
    }

private fun performRegister(){
    val username = username_register.text
    val email = email_register.text.toString()
    val password = password_register.text.toString()
    if(email.isNullOrEmpty() || password.isNullOrEmpty()){
        Toast.makeText(this, "Invalid Email / Password", Toast.LENGTH_SHORT).show()
        return
    }
//            Log.d("MainActivity", "Email $email")
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("MainActivity", "Success ${it.result?.user?.uid}")
                return@addOnCompleteListener
            }
        }
        .addOnFailureListener{
            Log.d("MainActivity", "Failed because of ${it.message}")
            return@addOnFailureListener
        }
    }
}