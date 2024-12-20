//package com.example.foodpro

//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import android.widget.TextView
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//
//class Mama : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_mama)
//
//        // Найдите TextView
//        val dataTextView: TextView = findViewById(R.id.dataTextView)
//
//        // Инициализация Firestore
//        val fs = Firebase.firestore
//
//        // Получение одного рецепта из коллекции "recipes"
//        fs.collection("recipes").limit(1).get()
//            .addOnSuccessListener { result ->
//                if (result.isEmpty) {
//                    dataTextView.text = "Данных нет"
//                } else {
//                    val recipe = result.documents[0].toObject(Recipes::class.java)
//                    if (recipe != null) {
//                        val displayText = """
//                            Название: ${recipe.title}
//                            Описание: ${recipe.description}
//                            Ингредиенты: ${recipe.ingredients.joinToString { "${it["item"]}: ${it["quantity"]}" }}
//                            Рецепт: ${recipe.recipe_instruction.joinToString { "${it["description"]}" }}
//                        """.trimIndent()
//                        dataTextView.text = displayText
//                    }
//                }
//            }
//            .addOnFailureListener { exception ->
//                dataTextView.text = "Ошибка: ${exception.message}"
//            }
//    }
//}

package com.example.foodpro

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Mama : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mama)

        // Найти виджеты в макете
        val recipeImageView: ImageView = findViewById(R.id.recipeImage) // Изображение рецепта
        val categoryTextView: TextView = findViewById(R.id.category) // Категория
        val cuisineTextView: TextView = findViewById(R.id.cuisine) // Кухня
        val menuTextView: TextView = findViewById(R.id.menu) // Меню
        val recipeTitleTextView: TextView = findViewById(R.id.recipeTitle) // Название рецепта
        val recipeAuthorTextView: TextView = findViewById(R.id.recipeAuthor) // Автор
        val recipeDetailsTextView: TextView = findViewById(R.id.recipeDetails) // Ингредиенты, время
        val recipeStatsTextView: TextView = findViewById(R.id.recipeStats) // Статистика

        // Инициализация Firestore
        val fs = Firebase.firestore

        // Получение одного рецепта из коллекции "recipes"
        fs.collection("recipes").limit(15).get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    recipeTitleTextView.text = "Данных нет"
                } else {
                    val recipe = result.documents[0].toObject(Recipe::class.java)
                    if (recipe != null) {
                        // Устанавливаем данные в TextView
                        categoryTextView.text = recipe.category
                        cuisineTextView.text = recipe.cuisine
                        menuTextView.text = recipe.menu
                        recipeTitleTextView.text = recipe.title
                        recipeAuthorTextView.text = "Автор: ${recipe.author}"
                        recipeDetailsTextView.text = "${recipe.`ingredients-count`} Ингредиентов, Время: ${recipe.time} мин"
                        recipeStatsTextView.text = "Likes: ${recipe.likes}, Dislikes: ${recipe.dislikes}, Bookmarks: ${recipe.bookmarks}"

                        // Используем Glide для загрузки изображения
                        Glide.with(this)
                            .load(recipe.image) // URL изображения
                            .into(recipeImageView) // Куда загружается изображение
                    } else {
                        recipeTitleTextView.text = "Ошибка загрузки рецепта"
                    }
                }
            }
            .addOnFailureListener { exception ->
                recipeTitleTextView.text = "Ошибка: ${exception.message}"
            }
    }
}



