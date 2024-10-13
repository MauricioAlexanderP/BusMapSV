package com.BusMap.busmapsv

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.auth.FirebaseAuth

class Activity_login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
           loginUser()
        }
        initLoadAds()
    }

    private fun initLoadAds() {
        val adRequest = AdRequest.Builder().build()
        val adView = findViewById<AdView>(R.id.bannerAd)
        adView.loadAd(adRequest)
    }

    private fun loginUser() {
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)

        val email = txtEmail.text.toString().trim()
        val password = txtPassword.text.toString().trim()

        // Validar que los campos no estén vacíos
        if (email.isEmpty()) {
            txtEmail.error = "El correo electrónico es requerido"
            return
        }

        if (password.isEmpty()) {
            txtPassword.error = "La contraseña es requerida"
            return
        }

        // Iniciar sesión con FirebaseAuth
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser

                    // Verificar si el usuario ha validado su correo
                    if (user != null && user.isEmailVerified) {
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show()
                        goToActivityHome()
                    } else {
                        Toast.makeText(this, "Por favor, verifica tu correo electrónico", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Manejar error de autenticación
                    Toast.makeText(this, "Error al iniciar sesión. Verifica tu correo y contraseña", Toast.LENGTH_LONG).show()
                }
            }
    }
    private fun authUser() {
        val btnLogin = findViewById<Button>(R.id.btnSignUp)
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        btnLogin.setOnClickListener {

            // Validar que el email no esté vacío y tenga un formato correcto
            val email = txtEmail.text.toString().trim()
            if (email.isEmpty()) {
                txtEmail.error = "El correo electrónico es requerido"
                return@setOnClickListener
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                txtEmail.error = "Formato de correo electrónico inválido"
                return@setOnClickListener
            }

            // Validar que las contraseñas no estén vacías
            val password = txtPassword.text.toString().trim()
            if (password.isEmpty()) {
                txtPassword.error = "La contraseña es requerida"
                return@setOnClickListener
            }

            validateUser()
        }
    }
    private fun validateUser() {
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        val email = txtEmail.text.toString().trim()
        val password = txtPassword.text.toString().trim()

        // Iniciar sesión con FirebaseAuth
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser

                    // Verificar si el usuario ha validado su correo
                    if (user != null && user.isEmailVerified) {
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show()
                        // Guardar el estado de inicio de sesión en SharedPreferences
                        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("is_logged_in", true)
                        editor.apply()

                        goToActivityHome()
                    } else {
                        Toast.makeText(this, "Por favor, verifica tu correo electrónico", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Manejar error de autenticación
                    Toast.makeText(this, "Error al iniciar sesión. Verifica tu correo y contraseña", Toast.LENGTH_LONG).show()
                }
            }
    }


    private fun goToActivityHome() {
        val intent = Intent(this, Activity_home::class.java)
        startActivity(intent)
    }
}