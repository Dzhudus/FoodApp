package com.example.foodpro

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private var currentPage = 1
    private val itemsPerPage = 15
    private var totalRecipes = 0
    private var totalPages = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        setupDrawer()
        setupNavigationView()

        loadUserProfileFromFirestore()

        fetchTotalRecipes()
        setupPaginationButtons()
        loadPageData(currentPage)
        openProfile()

        // Setup Search View with real-time search functionality
        val searchView: androidx.appcompat.widget.SearchView = findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchRecipes(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    searchRecipesInRealTime(newText)
                }
                return false
            }
        })

    }


    private fun searchRecipesInRealTime(query: String) {
        val fs = Firebase.firestore
        val recipesLayout: LinearLayout = findViewById(R.id.recipesLayout)
        recipesLayout.removeAllViews()

        fs.collection("recipes")
            .whereGreaterThanOrEqualTo("title", query)
            .whereLessThanOrEqualTo("title", query + "\uf8ff")  // `\uf8ff` is the Unicode character for 'tilde' (a character that is always after the end of any string in collation order)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    showToast("Error: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val recipesToShow = snapshots.documents.mapNotNull { it.toObject(Recipe::class.java) }
                    if (recipesToShow.isNotEmpty()) {
                        recipesToShow.forEach { recipe ->
                            recipesLayout.addView(createRecipeView(recipe))
                        }
                    } else {
                        Toast.makeText(this, "No recipes found.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun searchRecipes(query: String) {
        val fs = Firebase.firestore
        val recipesLayout: LinearLayout = findViewById(R.id.recipesLayout)
        recipesLayout.removeAllViews()

        fs.collection("recipes")
            .whereEqualTo("title", query)
            .get()
            .addOnSuccessListener { result ->
                val recipesToShow = result.documents.mapNotNull { it.toObject(Recipe::class.java) }
                if (recipesToShow.isNotEmpty()) {
                    recipesToShow.forEach { recipe ->
                        recipesLayout.addView(createRecipeView(recipe))
                    }
                } else {
                    Toast.makeText(this, "No recipes found.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                showToast("Error: ${e.message}")
            }
    }





///////////////////

    /////// Получает общее количество рецептов из Firestore и вычисляет количество страниц
    private fun fetchTotalRecipes() {
        val fs = Firebase.firestore
        fs.collection("recipes").get().addOnSuccessListener { result ->
            totalRecipes = result.size()
            totalPages = (totalRecipes + itemsPerPage - 1) / itemsPerPage
            updatePageIndicator()
        }
    }

    // Загружает данные рецептов для указанной страницы
    private fun loadPageData(page: Int) {
        val fs = Firebase.firestore
        val recipesLayout: LinearLayout = findViewById(R.id.recipesLayout)
        recipesLayout.removeAllViews()

        val query: Query = fs.collection("recipes")
            .limit((page * itemsPerPage).toLong())

        query.get().addOnSuccessListener { result ->
            val recipesToShow = result.documents.takeLast(itemsPerPage)
            recipesToShow.forEach { document ->
                val recipe = document.toObject(Recipe::class.java)
                recipe?.let {
                    recipesLayout.addView(createRecipeView(it))
                }
            }
        }
    }

    // Создаёт и возвращает View для отображения рецепта с заполненными данными
    private fun createRecipeView(recipe: Recipe): View {
        val recipeView = layoutInflater.inflate(R.layout.activity_mama2, null)

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
        recipeView.findViewById<Button>(R.id.save_button).setOnClickListener { saveRecipeToFavorites(recipe) }

        recipeView.setOnClickListener {
            val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("RECIPE", recipe)
            }
            startActivity(intent)
        }

        return recipeView
    }

    // Сохраняет рецепт в избранное пользователя в Firestore
    private fun saveRecipeToFavorites(recipe: Recipe) {
        val currentUser = Firebase.auth.currentUser ?: return showToast("Пользователь не авторизован.")

        Firebase.firestore.collection("users").document(currentUser.uid)
            .collection("favorites").add(recipe)
            .addOnSuccessListener { showToast("Рецепт сохранён в Избранное!") }
            .addOnFailureListener { e -> showToast("Ошибка: ${e.message}") }
    }

    // Показывает всплывающее сообщение (Toast) с заданным текстом
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Настраивает кнопки пагинации для перехода между страницами
    private fun setupPaginationButtons() {
        findViewById<ImageButton>(R.id.prevButton).setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                loadPageData(currentPage)
                updatePageIndicator()
            }
        }

        findViewById<ImageButton>(R.id.nextButton).setOnClickListener {
            if (currentPage < totalPages) {
                currentPage++
                loadPageData(currentPage)
                updatePageIndicator()
            }
        }
    }

    // Обновляет индикатор текущей страницы
    private fun updatePageIndicator() {
        findViewById<TextView>(R.id.pageIndicator).text = "Страница $currentPage из $totalPages"
    }

    // Настраивает тулбар (верхняя панель)
    private fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    // Настраивает выдвижное меню (DrawerLayout)
    private fun setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, findViewById(R.id.toolbar), R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    // Настраивает действия для элементов в навигационном меню
    private fun setupNavigationView() {
        findViewById<NavigationView>(R.id.nav_view).setNavigationItemSelectedListener { item ->
            handleNavigationItemClick(item)
            true
        }
    }

    // Обрабатывает нажатия на элементы в навигационном меню
    private fun handleNavigationItemClick(item: MenuItem) {
        when (item.itemId) {
            R.id.nav_home -> startActivity(Intent(this,MainActivity::class.java))
            R.id.nav_favorite -> startActivity(Intent(this, FavoritesActivity::class.java))
            R.id.nav_edit -> startActivity(Intent(this, EditActivity::class.java))
        }
        drawerLayout.closeDrawers()
    }


    private fun loadUserProfileFromFirestore() {
        val user = Firebase.auth.currentUser

        if (user != null) {
            val userId = user.uid // Получаем уникальный идентификатор пользователя
            val firestore = Firebase.firestore

            // Ссылка на документ пользователя
            val userDocRef = firestore.collection("users").document(userId)

            // Получение документа из Firestore
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userName = document.getString("name") ?: "Неизвестный пользователь"
                        val userEmail = document.getString("email") ?: "Неизвестный email"

                        // Обновляем интерфейс
                        val navView: NavigationView = findViewById(R.id.nav_view)
                        val headerView: View = navView.getHeaderView(0)
                        val nameTextView: TextView = headerView.findViewById(R.id.userNameTextView)
                        val emailTextView: TextView = headerView.findViewById(R.id.userEmailTextView)

                        nameTextView.text = userName
                        emailTextView.text = userEmail
                    } else {
                        Toast.makeText(this, "Документ пользователя отсутствует", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка при загрузке данных: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openProfile(){
        val profile = findViewById<ImageView>(R.id.profile_icon)

        profile.setOnClickListener{
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }
    }














}