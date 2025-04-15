package com.gammadesv.demoleapp.restaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gammadesv.demoleapp.databinding.ActivityRestaurantDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class RestaurantDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestaurantDashboardBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.btnCreatePromo.setOnClickListener {
            startActivity(Intent(this, CreatePromotionActivity::class.java))
        }

        binding.btnManagePromos.setOnClickListener {
            startActivity(Intent(this, PromotionsListActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            finish()
        }
    }
}