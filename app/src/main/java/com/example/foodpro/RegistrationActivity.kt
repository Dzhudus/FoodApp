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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseUser

class RegistrationActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Toast.makeText(this, "Добро пожаловать, ${user.email}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        firebaseAuth = Firebase.auth

        val nameInput: EditText = findViewById(R.id.nameInput)
        val emailInput: EditText = findViewById(R.id.emailInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val registerButton: Button = findViewById(R.id.registerButton)
        val backButton: Button = findViewById(R.id.backButton)

        val textView: TextView = findViewById(R.id.alreadyHaveAccount)
        val spannableText = SpannableString(getString(R.string.have_account))

        // Сделать "Зарегистрироваться" красным
        val redColorSpan = ForegroundColorSpan(Color.RED)
        spannableText.setSpan(
            redColorSpan,
            17, // Начало слова "Зарегистрироваться"
            spannableText.length, // Конец слова
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Сделать "Зарегистрироваться" кликабельным
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
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

        registerButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val name = nameInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Snackbar.make(it, "Пожалуйста, заполните все поля", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .setTextColor(Color.WHITE)
                    .show()
            } else if (password.length < 6) {
                Snackbar.make(it, "Пароль должен быть не менее 6 символов", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .setTextColor(Color.WHITE)
                    .show()
            } else {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            user?.let {
                                saveUserInfoToFirestore(it.uid, name, email)
                            }

                            Snackbar.make(it, "Успешная регистрация!", Snackbar.LENGTH_LONG)
                                .setBackgroundTint(Color.GREEN)
                                .setTextColor(Color.WHITE)
                                .show()

                            updateUI(user)
                        } else {
                            Snackbar.make(it, "Ошибка регистрации: ${task.exception?.message}", Snackbar.LENGTH_LONG)
                                .setBackgroundTint(Color.RED)
                                .setTextColor(Color.WHITE)
                                .show()

                            updateUI(null)
                        }
                    }
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveUserInfoToFirestore(uid: String, name: String, email: String) {
        val db = Firebase.firestore
        val user = mapOf(
            "name" to name,
            "email" to email
        )
        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "Данные пользователя успешно сохранены.")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Ошибка сохранения данных пользователя: ${e.message}")
            }
    }
}
