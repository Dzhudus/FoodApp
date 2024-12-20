package com.example.foodpro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FavoritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        loadFavoritesForCurrentUser()
    }

    private fun loadFavoritesForCurrentUser() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            showToast("Пользователь не авторизован.")
            return
        }

        val userId = currentUser.uid
        val fs = Firebase.firestore
        val favoritesLayout: LinearLayout = findViewById(R.id.favoritesLayout)

        fs.collection("users")
            .document(userId)
            .collection("favorites")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val recipe = document.toObject(Recipe::class.java)
                    if (recipe != null) {
                        val recipeView = createRecipeView(recipe)
                        favoritesLayout.addView(recipeView)
                    }
                }
            }
            .addOnFailureListener { e ->
                showToast("Ошибка загрузки: ${e.message}")
            }
    }

    private fun createRecipeView(recipe: Recipe): View {
        val recipeView = layoutInflater.inflate(R.layout.activity_mama, null)

        val recipeImageView: ImageView = recipeView.findViewById(R.id.recipeImage)
        Glide.with(this).load(recipe.image).into(recipeImageView)
        recipeView.findViewById<TextView>(R.id.category).text = recipe.category
        recipeView.findViewById<TextView>(R.id.cuisine).text = recipe.cuisine
        recipeView.findViewById<TextView>(R.id.menu).text = recipe.menu
        recipeView.findViewById<TextView>(R.id.recipeTitle).text = recipe.title

        recipeView.findViewById<TextView>(R.id.recipeAuthor).text = "Автор: ${recipe.author}"
        recipeView.findViewById<TextView>(R.id.recipePortions).text = "${recipe.`ingredients-count`}"
        recipeView.findViewById<TextView>(R.id.recipeIngredients).text = "${recipe.`ingredients-count`} "

        recipeView.findViewById<TextView>(R.id.recipeTime).text = "${recipe.time}"
        recipeView.findViewById<TextView>(R.id.recipeStats).text = "L:${recipe.likes}, D: ${recipe.dislikes}, B: ${recipe.bookmarks}"
        recipeView.findViewById<Button>(R.id.fafa).setOnClickListener { deleteFromFavorites(recipe) }

        recipeView.setOnClickListener {
            val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("RECIPE", recipe)
            }
            startActivity(intent)
        }

        return recipeView
    }

    private fun deleteFromFavorites(recipe: Recipe) {
        val currentUser = Firebase.auth.currentUser ?: return showToast("Пользователь не авторизован.")

        val fs = Firebase.firestore
        val button = findViewById<Button>(R.id.save_button)

        // Find the document ID of the recipe to delete
        fs.collection("users")
            .document(currentUser.uid)
            .collection("favorites")
            .whereEqualTo("recipeId", recipe.id) // Assuming each recipe has a unique 'id' field
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val docId = documents.documents[0].id // Get the ID of the first document found
                    fs.collection("users").document(currentUser.uid)
                        .collection("favorites").document(docId)
                        .delete()
                        .addOnSuccessListener {
                            showToast("Рецепт удалён из Избранного!")
                            button.isSelected = false // Reset button to its default state
                            button.text = "Сохранить" // Reset button text
                        }
                        .addOnFailureListener { e ->
                            showToast("Ошибка удаления: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                showToast("Ошибка загрузки: ${e.message}")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
