package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.CallbackManager.*
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.util.Base64
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class LoginActivity : AppCompatActivity(){

    private val RC_SIGN_IN: Int = 99
    private val TAG: String = "login01"
    private lateinit var auth: FirebaseAuth
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        newAcc.setOnClickListener{
            var intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
        forget.setOnClickListener{
            var intent = Intent(this,ForgetActivity::class.java)
            startActivity(intent)
        }

        next_button.setOnClickListener{
            var flag1: Boolean? = null
                    flag1 = true
            var flag2: Boolean? = null
                    flag2 = true
            if(username_edit_text.text.isNullOrBlank()){
                username_text_input.error = "Enter Email Address"
                flag1 = false
            }else{
                flag1 = true
                username_text_input.error = "";
            }
            if (password_edit_text.text.isNullOrBlank()&& password_edit_text.text!!.length > 6){
                password_text_input.error = "Password must be greater then 6"
                flag2 = false
            }
            else{
                flag2 = true
                password_text_input.error = ""
            }
            if (flag1 && flag2){
                auth.signInWithEmailAndPassword(username_edit_text.text.toString(),password_edit_text.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this,"success",Toast.LENGTH_SHORT).show()
                                val firebaseUser = task.result!!.user
                                val emailVerified = firebaseUser.isEmailVerified
                                startMainActivity()


                                if (!emailVerified) {} //manage your email verification
                            } else {
                                Toast.makeText(this,"Error occur",Toast.LENGTH_SHORT).show()
                                Log.v("firebase010",task.exception.toString())
                                //Manage error
                            }
                        }
            }
        }
        sign_in_button.setOnClickListener {
            signIn()
        }

        callbackManager = Factory.create()

        fblogin_button.setReadPermissions("email", "public_profile", "user_friends")
        fblogin_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                // ...
            }
        })

        fbNONlogin_button.setOnClickListener{
            fblogin_button.callOnClick()
        }

    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun startMainActivity(){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
//        updateUI(currentUser)

        currentUser?.let {
            startMainActivity()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }

            callbackManager.onActivityResult(requestCode, resultCode, data)

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        startMainActivity()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Snackbar.make(login_main, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    }

                    // ...
                }
    }


    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        startMainActivity()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }

                    // ...
                }
    }
}
