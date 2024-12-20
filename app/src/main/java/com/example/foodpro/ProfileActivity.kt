package com.example.foodpro

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton


class ProfileActivity : AppCompatActivity() {
    private var favoriteButton: MaterialButton? = null
    private var editRecipeButton: MaterialButton? = null
    private var logoutButton: MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize buttons
        favoriteButton = findViewById(R.id.favoriteButton)
        editRecipeButton = findViewById(R.id.editRecipeButton)
        logoutButton = findViewById(R.id.logoutButton)

        // Set up click listeners
        setUpButtonListeners()
    }

    private fun setUpButtonListeners() {
        // Navigate to FavoritesActivity when "Favorite" button is clicked
        favoriteButton!!.setOnClickListener { v: View? ->
            val intent =
                Intent(
                    this@ProfileActivity,
                    FavoritesActivity::class.java
                )
            startActivity(intent)
        }

        // Navigate to EditActivity when "Edit Recipe" button is clicked
        editRecipeButton!!.setOnClickListener { v: View? ->
            val intent =
                Intent(
                    this@ProfileActivity,
                    EditActivity::class.java
                )
            startActivity(intent)
        }

        logoutButton!!.setOnClickListener { v: View? ->


            // Redirect to WelcomeActivity
            val intent =
                Intent(
                    this@ProfileActivity,
                    WelcomeActivity::class.java
                )
            startActivity(intent)
            finish() 
        }
    }
}
