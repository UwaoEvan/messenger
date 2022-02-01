package com.evan.mymessenger.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.evan.mymessenger.R
import com.evan.mymessenger.messages.DashboardActivity
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
                    Log.d("LoginActivity", "Welcome user id ${it.result?.user?.email}")
                    var intent = Intent(this, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK. or (Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener{
                Toast.makeText(this, "Invalid details", Toast.LENGTH_SHORT).show()
                Log.d("LoginActivity", "Failed ${it.message}")
                return@addOnFailureListener
            }
    }
}
