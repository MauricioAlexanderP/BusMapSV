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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class Activity_SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authUser()
    }

    private fun authUser() {
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val txtName = findViewById<EditText>(R.id.txtNombre)
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        val txtConfirmarPassword = findViewById<EditText>(R.id.txtConfirmarPasswored)

        btnSignUp.setOnClickListener {
            // Validar que el nombre no esté vacío
            val name = txtName.text.toString().trim()
            if (name.isEmpty()) {
                txtName.error = "El nombre es requerido"
                return@setOnClickListener
            }

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
            val confirmPassword = txtConfirmarPassword.text.toString().trim()

            if (password.isEmpty()) {
                txtPassword.error = "La contraseña es requerida"
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                txtConfirmarPassword.error = "La confirmación de la contraseña es requerida"
                return@setOnClickListener
            }

            // Validar que la contraseña tenga al menos 8 caracteres, contenga mayúsculas, minúsculas, números y símbolos
            if (password.length < 8) {
                txtPassword.error = "La contraseña debe tener al menos 8 caracteres"
                return@setOnClickListener
            }

            val passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!.]).{8,}\$")
            if (!passwordPattern.matcher(password).matches()) {
                txtPassword.error = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un símbolo especial"
                return@setOnClickListener
            }

            // Validar que ambas contraseñas coincidan
            if (password != confirmPassword) {
                txtConfirmarPassword.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            }

            // Evitar contraseñas comunes (lista básica, puedes ampliarla)
            val commonPasswords = listOf("123456", "password", "123456789", "qwerty", "12345678")
            if (commonPasswords.contains(password)) {
                txtPassword.error = "La contraseña es demasiado común, elige una más segura"
                return@setOnClickListener
            }

            // Si todas las validaciones pasan, redirigir a la siguiente actividad
            addUsers()

        }
    }

    private fun goToActivityHome() {
        val intent = Intent(this, Activity_home::class.java)
        startActivity(intent)
    }

    private fun addUsers() {
        val txtName = findViewById<EditText>(R.id.txtNombre)
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        val name = txtName.text.toString().trim()
        val email = txtEmail.text.toString().trim()
        val password = txtPassword.text.toString().trim()

        // Crear el usuario en FirebaseAuth
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Obtener el ID del usuario registrado
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                        if (verifyTask.isSuccessful) {
                            // Guardar los datos del usuario en Firestore
                            val userId = user.uid
                            val db = FirebaseFirestore.getInstance()
                            val userData = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "password" to password // Puedes considerar no almacenar la contraseña en texto plano
                            )

                            // Guardar la información del usuario en la colección "users"
                            db.collection("users")
                                .document(userId)
                                .set(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Usuario registrado correctamente, verifica tu correo electrónico", Toast.LENGTH_LONG).show()
                                    goToActivityHome()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error al guardar la información del usuario", Toast.LENGTH_LONG).show()
                                }
                        } else {
                            Toast.makeText(this, "Error al enviar verificación de correo", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_LONG).show()
                }
            }
    }

}