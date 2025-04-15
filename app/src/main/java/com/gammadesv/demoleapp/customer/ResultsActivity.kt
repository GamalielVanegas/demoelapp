package com.gammadesv.demoleapp.customer

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.gammadesv.demoleapp.databinding.ActivityResultsBinding
import com.gammadesv.demoleapp.models.Restaurant
import com.gammadesv.demoleapp.models.SearchFilters
import com.google.firebase.firestore.FirebaseFirestore

class ResultsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultsBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: RestaurantAdapter
    private var filters = SearchFilters()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filters = intent.getSerializableExtra("filters") as SearchFilters
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        loadRestaurants()
        setupTitle()
    }

    private fun setupTitle() {
        val title = "Tu selección: " +
                "${if (filters.department != "Seleccione") "En ${filters.department}, " else ""}" +
                "${if (filters.foodType != "Seleccione") "comida ${filters.foodType}, " else ""}" +
                "${if (filters.promotionType != "Seleccione") "${filters.promotionType}, " else ""}" +
                "${if (filters.environment != "Seleccione") "${filters.environment}" else ""}"

        binding.tvSelection.text = title
    }

    private fun setupRecyclerView() {
        adapter = RestaurantAdapter { restaurant ->
            // Abrir Google Maps con la ubicación
            val gmmIntentUri = Uri.parse(restaurant.mapUrl)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        binding.rvResults.layoutManager = LinearLayoutManager(this)
        binding.rvResults.adapter = adapter
    }

    private fun loadRestaurants() {
        var query = db.collection("restaurants")

        if (filters.department != "Seleccione") {
            query = query.whereEqualTo("department", filters.department)
        }
        if (filters.foodType != "Seleccione") {
            query = query.whereEqualTo("foodType", filters.foodType)
        }
        if (filters.promotionType != "Seleccione") {
            query = query.whereEqualTo("promotionType", filters.promotionType)
        }
        if (filters.environment != "Seleccione") {
            query = query.whereEqualTo("environment", filters.environment)
        }

        query.get()
            .addOnSuccessListener { documents ->
                val restaurants = mutableListOf<Restaurant>()
                for (document in documents) {
                    val restaurant = document.toObject(Restaurant::class.java)
                    restaurants.add(restaurant)
                }
                adapter.submitList(restaurants)
            }
            .addOnFailureListener { exception ->
                // Manejar error
            }
    }
}