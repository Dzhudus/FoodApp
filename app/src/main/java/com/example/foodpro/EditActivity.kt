package com.example.foodpro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var caloriesEditText: EditText
    private lateinit var carbohydratesEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var cuisineEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var fatsEditText: EditText
    private lateinit var ingredientsEditText: EditText
    private lateinit var ingredientsCountEditText: EditText
    private lateinit var proteinsEditText: EditText
    private lateinit var recipeInstructionsEditText: EditText
    private lateinit var titleEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Bind views
        caloriesEditText = findViewById(R.id.caloriesEditText)
        carbohydratesEditText = findViewById(R.id.carbohydratesEditText)
        categoryEditText = findViewById(R.id.categoryEditText)
        cuisineEditText = findViewById(R.id.cuisineEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        fatsEditText = findViewById(R.id.fatsEditText)
        ingredientsEditText = findViewById(R.id.ingredientsEditText)
        ingredientsCountEditText = findViewById(R.id.ingredientsCountEditText)
        proteinsEditText = findViewById(R.id.proteinsEditText)
        recipeInstructionsEditText = findViewById(R.id.recipeInstructionsEditText)
        submitButton = findViewById(R.id.submitButton)
        titleEditText = findViewById(R.id.titleEditText)

        // Set the submit button click listener
        submitButton.setOnClickListener {
            submitRecipe()
        }
    }

    private fun submitRecipe() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        // Collect data from input fields
        val recipe = hashMapOf(
            "author" to (currentUser.displayName ?: "Unknown Author"), // Automatically insert current user's name
            "title" to titleEditText.text.toString(),
            "description" to descriptionEditText.text.toString(),
            "category" to categoryEditText.text.toString(),
            "cuisine" to cuisineEditText.text.toString(),
            "calories" to caloriesEditText.text.toString(),
            "proteins" to proteinsEditText.text.toString(),
            "fats" to fatsEditText.text.toString(),
            "carbohydrates" to carbohydratesEditText.text.toString(),
            "ingredients" to ingredientsEditText.text.toString(),
            "instructions" to recipeInstructionsEditText.text.toString()
        )

        // Save the recipe to the "recipes" collection in Firestore
        firestore.collection("ggg")
            .add(recipe)
            .addOnSuccessListener { documentReference ->
                // Recipe submitted successfully, now attach to user's favorites
                attachRecipeToUser(currentUser.uid, documentReference.id)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to submit recipe: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun attachRecipeToUser(userId: String, recipeId: String) {
        val userRef = firestore.collection("users").document(userId).collection("mytoprecipe").document(recipeId)
        val recipeRef = firestore.collection("ggg").document(recipeId)

        userRef.set(hashMapOf("recipeId" to recipeRef))
            .addOnSuccessListener {
                Toast.makeText(this, "Recipe attached to your favorites", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to attach recipe: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
