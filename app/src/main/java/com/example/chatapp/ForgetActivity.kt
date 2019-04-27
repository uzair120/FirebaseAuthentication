package com.example.chatapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forget.*

class ForgetActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)
        mAuth = FirebaseAuth.getInstance()

        btnReset.setOnClickListener{
            var flag1: Boolean?

            if(username_edit_text.text.isNullOrBlank()){
                username_text_input.error = "Enter Email Address"
                flag1 = false
            }else{
                flag1 = true
                username_text_input.error = "";
            }

            if (flag1 ){
                mAuth.sendPasswordResetEmail(username_edit_text.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this,"Email sent Successfully",Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this,"Email not found",Toast.LENGTH_SHORT).show()
                            }
                        }
            }
        }
    }
}
