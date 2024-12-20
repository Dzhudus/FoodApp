package com.example.foodpro

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = Firebase.auth

        val emailInput: EditText = findViewById(R.id.emailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val loginButton: Button = findViewById(R.id.loginButton)
        val backButton: Button = findViewById(R.id.backButton)
        val textViewDontHaveAccount: TextView = findViewById(R.id.dontHaveAccount)

        // Setup for the clickable "Don't have an account? Register here" text
        setupClickableSpan(textViewDontHaveAccount)

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showSnackbar(it, "Please fill in all fields", Color.RED)
            } else {
                loginUser(email, password, it)
            }
        }

        // Handle back button click
        backButton.setOnClickListener {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }

    private fun setupClickableSpan(textView: TextView) {
        val spannableText = SpannableString(getString(R.string.dont_have_account))
        val redColorSpan = ForegroundColorSpan(Color.RED)
        spannableText.setSpan(
            redColorSpan,
            17, // Start of "Register here"
            spannableText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
            }
        }
        spannableText.setSpan(
            clickableSpan,
            17,
            spannableText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannableText
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun loginUser(email: String, password: String, view: View) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = firebaseAuth.currentUser
                    if (user?.email == "admin1@gmail.com") {
                        showSnackbar(view, "Welcome, Admin!", Color.GREEN)
                        startActivity(Intent(this, AdminActivity::class.java))
                    } else {
                        showSnackbar(view, "Welcome back, ${user?.email}", Color.GREEN)
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                    finish()
                } else {
                    val errorMessage = task.exception?.message ?: "Login error"
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    showSnackbar(view, "Error: $errorMessage", Color.RED)
                }
            }
    }

    private fun showSnackbar(view: View, message: String, color: Int) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(color)
            .setTextColor(Color.WHITE)
            .show()
    }
}