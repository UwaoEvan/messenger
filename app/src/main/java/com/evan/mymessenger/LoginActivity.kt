package com.evan.mymessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val login = signUp_text
        login.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }

        val loginBtn = button_login
        loginBtn.setOnClickListener{
            performLogin()
        }
    }

    private fun performLogin(){
        val email = email_login.text.toString()
        val password = password_login.text.toString()
        if(email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid Email /Password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    Log.d("LoginActivity", "Welcome ${it.result?.user?.email}")
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener{
                Log.d("LoginActivity", "Failed")
                return@addOnFailureListener
            }
    }
}
