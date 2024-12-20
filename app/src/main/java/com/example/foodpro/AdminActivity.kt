package com.example.foodpro

import android.widget.Button
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AdminActivity : AppCompatActivity() {

    private lateinit var displayPanel: LinearLayout
    private lateinit var showUsersButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        displayPanel = findViewById(R.id.displayPanel)
        showUsersButton = findViewById(R.id.showUsers)
        auth = FirebaseAuth.getInstance()

        showUsersButton.setOnClickListener {
            fetchAndDisplayUsers()
        }
    }

    private fun fetchAndDisplayUsers() {
        auth.fetchSignInMethodsForEmail("*") // '*' will match any email address
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    val signInMethods = result?.signInMethods

                    displayPanel.removeAllViews() // Clear any previous users displayed

                    if (signInMethods != null && signInMethods.isNotEmpty()) {
                        for (email in signInMethods) {
                            // Validate if the email address is correctly formatted
                            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                val userTextView = TextView(this)
                                userTextView.text = email
                                displayPanel.addView(userTextView)
                            }
                        }
                    } else {
                        val noUsersTextView = TextView(this)
                        noUsersTextView.text = "No registered users found."
                        displayPanel.addView(noUsersTextView)
                    }
                } else {
                    // Handle error
                    val errorTextView = TextView(this)
                    errorTextView.text = "Failed to fetch users: ${task.exception?.message}"
                    displayPanel.addView(errorTextView)
                }
            }
    }
}



