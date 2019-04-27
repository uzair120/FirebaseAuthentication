package com.example.chatapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.password_edit_text
import kotlinx.android.synthetic.main.activity_sign_up.password_text_input
import kotlinx.android.synthetic.main.activity_sign_up.username_edit_text
import kotlinx.android.synthetic.main.activity_sign_up.username_text_input

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()

        btnSignUp.setOnClickListener {
            var flag1: Boolean?
            flag1 = true
            var flag3: Boolean?
            flag3 = true
            var flag2: Boolean?
            flag2 = true
            if (username_edit_text.text.isNullOrBlank()) {
                username_text_input.error = "Enter Email Address"
                flag1 = false
            } else {
                flag1 = true
                username_text_input.error = "";
            }
            if (password_edit_text.text.isNullOrBlank() && password_edit_text.text!!.length > 6) {
                password_text_input.error = "Password must be greater then 6"
                flag2 = false
            } else {
                flag2 = true
                password_text_input.error = ""
            }
            if (name_edit_text.text.isNullOrBlank()) {
                name_text_input.error = "Please Enter Your Name"
                flag3 = false
            } else {
                flag3 = true
                name_text_input.error = ""
            }
            if (flag1 && flag2 && flag3) {

                auth.createUserWithEmailAndPassword(username_edit_text.text.toString(),password_edit_text.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this,"success", Toast.LENGTH_SHORT).show()
                                val firebaseUser = task.result!!.user
                                val emailVerified = firebaseUser.isEmailVerified
                                startActivity()


                                if (!emailVerified) {} //manage your email verification
                            } else {
                                Toast.makeText(this,"Error occur", Toast.LENGTH_SHORT).show()
                                Log.v("firebase010",task.exception.toString())
                                //Manage error
                            }
                        }
            }
        }
    }

    private fun startActivity(){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
}
