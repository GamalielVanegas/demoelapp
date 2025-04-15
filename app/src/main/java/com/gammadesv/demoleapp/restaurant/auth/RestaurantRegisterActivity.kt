package com.gammadesv.demoleapp.restaurant.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gammadesv.demoleapp.databinding.ActivityRestaurantRegisterBinding
import com.gammadesv.demoleapp.models.Restaurant
import com.gammadesv.demoleapp.restaurant.RestaurantDashboardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RestaurantRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestaurantRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnRegister.setOnClickListener { registerRestaurant() }
    }

    private fun registerRestaurant() {
        val name = binding.etName.text.toString()
        val address = binding.etAddress.text.toString()
        val phone = binding.etPhone.text.toString()
        val manager = binding.etManager.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val website = binding.etWebsite.text.toString()
        val facebook = binding.etFacebook.text.toString()
        val instagram = binding.etInstagram.text.toString()

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Nombre, email y contraseña son requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val restaurant = Restaurant(
                        id = auth.currentUser?.uid ?: "",
                        name = name,
                        address = address,
                        phone = phone,
                        manager = manager,
                        email = email,
                        website = website,
                        facebook = facebook,
                        instagram = instagram
                    )

                    saveRestaurantData(restaurant)
                } else {
                    Toast.makeText(this, "Registro fallido: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveRestaurantData(restaurant: Restaurant) {
        db.collection("restaurants").document(restaurant.id)
            .set(restaurant)
            .addOnSuccessListener {
                startActivity(Intent(this, RestaurantDashboardActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error guardando datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}