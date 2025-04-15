package com.gammadesv.demoleapp.restaurant.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.gammadesv.demoleapp.databinding.ActivityRestaurantAuthBinding
import com.gammadesv.demoleapp.restaurant.RestaurantDashboardActivity
import com.google.firebase.auth.FirebaseAuth

class RestaurantAuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestaurantAuthBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener { loginRestaurant() }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RestaurantRegisterActivity::class.java))
        }
    }

    private fun loginRestaurant() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email y contraseña requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, RestaurantDashboardActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                } else {
                    Toast.makeText(this, "Autenticación fallida", Toast.LENGTH_SHORT).show()
                }
            }
    }
}