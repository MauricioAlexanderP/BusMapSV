package com.BusMap.busmapsv

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern


class Activity_SignUp : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configura Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))  // Obtén esto desde Firebase Console
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //Configura el botón de inicio de sesión con Google
        val googleSignUpButton = findViewById<Button>(R.id.btnGoogleSignUp)

        //al hacer clic en el botón de inicio de sesión con Google, inicia el proceso de inicio de sesión con Google
        googleSignUpButton.setOnClickListener {
            signInWithGoogle()
        }

        authUser()
    }
    // Iniciar el flujo de inicio de sesión con Google
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // Capturar el resultado del inicio de sesión
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("SignInActivity", "Google sign in failed", e)
                Toast.makeText(this, "Error en el inicio de sesión con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Autenticación en Firebase con el token de Google
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    val user = FirebaseAuth.getInstance().currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(this, "Error de autenticación con Google", Toast.LENGTH_SHORT).show()
                }
            }
    }
    // Actualizar la interfaz del usuario después del inicio de sesión
    private fun updateUI(user: FirebaseUser?) {
        user?.let {
            Toast.makeText(this, "Bienvenido ${user.displayName}", Toast.LENGTH_SHORT).show()
            // Redirigir a la actividad principal
            goToActivityHome()
        }
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
        finish()
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
                                "password" to password // Considera no almacenar la contraseña en texto plano
                            )

                            // Guardar la información del usuario en la colección "users"
                            db.collection("users")
                                .document(userId)
                                .set(userData)
                                .addOnSuccessListener {

                                    // Guardar el estado de inicio de sesión en SharedPreferences
                                    val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.putBoolean("is_logged_in", true)
                                    editor.apply()

                                    showVerificationDialog()
                                    //goToActivityHome() // Llamada para ir a la pantalla principal


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

    private fun showVerificationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Verificación de Correo")
        builder.setMessage("Usuario registrado correctamente. Por favor, verifica tu correo electrónico para continuar.")

        // Botón "Aceptar" que cierra el diálogo
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()  // Cierra el diálogo

            // Llamada a la función que redirige a la actividad principal
            goToActivityHome()
        }

        // Crear y mostrar el diálogo
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}