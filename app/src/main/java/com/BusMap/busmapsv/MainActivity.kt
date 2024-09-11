package com.BusMap.busmapsv

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        btnIngresar.setOnClickListener {
            goToActivityLogin()
        }
        val btnRegistrarte = findViewById<Button>(R.id.btnRegistrarte)
        btnRegistrarte.setOnClickListener {
            goToActivitySignUp()
        }
    }

    private fun goToActivitySignUp() {
    val intent = Intent(this, Activity_SignUp::class.java)
    startActivity(intent)
    }

    fun goToActivityLogin() {
        val intent = Intent(this, Activity_login::class.java)
        startActivity(intent)
    }
}