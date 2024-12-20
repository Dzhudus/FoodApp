package com.example.foodpro

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class RecipeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        val recipe = intent.getParcelableExtra<Recipe>("RECIPE")

        recipe?.let {
            findViewById<TextView>(R.id.recipeTitle).text = it.title
            findViewById<TextView>(R.id.recipeDescription).text = it.description
            findViewById<TextView>(R.id.recipeAuthor).text = "Автор: ${it.author}"
            findViewById<TextView>(R.id.recipeStats).text =
                "Likes: ${it.likes}, Dislikes: ${it.dislikes}, Bookmarks: ${it.bookmarks}"
            findViewById<ImageView>(R.id.recipeImage).let { imageView ->
                Glide.with(this).load(it.image).into(imageView)
            }

            // Отображение ингредиентов
            val ingredientsLayout: LinearLayout = findViewById(R.id.ingredientsLayout)
            it.ingredients.forEach { ingredient ->
                val ingredientView = layoutInflater.inflate(R.layout.ingredient_item, null)
                ingredientView.findViewById<TextView>(R.id.ingredientName).text = ingredient.item
                ingredientView.findViewById<TextView>(R.id.ingredientQuantity).text = ingredient.quantity
                ingredientsLayout.addView(ingredientView)
            }

            // Отображение инструкций
            val instructionsLayout: LinearLayout = findViewById(R.id.instructionsLayout)
            it.recipeInstruction.forEach { instruction ->
                val instructionView = layoutInflater.inflate(R.layout.instruction_item, null)
                instructionView.findViewById<TextView>(R.id.stepDescription).text = instruction.description
                instruction.stepImage?.let { imageUrl ->
                    val stepImageView = instructionView.findViewById<ImageView>(R.id.stepImage)
                    Glide.with(this).load(imageUrl).into(stepImageView)
                }
                instructionsLayout.addView(instructionView)
            }
        }
    }
}
