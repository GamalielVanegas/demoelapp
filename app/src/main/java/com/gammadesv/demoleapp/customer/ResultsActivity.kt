package com.gammadesv.demoleapp.customer

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gammadesv.demoleapp.databinding.ActivityResultsBinding
import com.gammadesv.demoleapp.adapters.RestaurantAdapter
import com.gammadesv.demoleapp.models.Restaurant
import com.gammadesv.demoleapp.models.SearchFilters
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.widget.Toast
import com.google.firebase.firestore.Query

class ResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultsBinding
    private lateinit var adapter: RestaurantAdapter
    private lateinit var filters: SearchFilters

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filters = intent.getParcelableExtra("search_filters") ?: SearchFilters()

        setupUI()
        loadRestaurants()
    }

    private fun setupUI() {
        binding.tvSelection.text = buildSelectionTitle()
        setupRecyclerView()
    }

    private fun buildSelectionTitle(): String {
        return listOf(
            filters.department.takeIf { it != "Seleccione" }?.let { "En $it" },
            filters.foodType.takeIf { it != "Seleccione" }?.let { "comida $it" },
            filters.promotionType.takeIf { it != "Seleccione" },
            filters.environment.takeIf { it != "Seleccione" }
        )
            .filterNotNull()
            .joinToString(", ")
            .takeIf { it.isNotEmpty() }
            ?.let { "Tu selección: $it" } ?: "Todos los restaurantes"
    }

    private fun setupRecyclerView() {
        adapter = RestaurantAdapter { restaurant ->
            openMaps(restaurant.mapUrl)
        }

        binding.rvResults.apply {
            layoutManager = LinearLayoutManager(this@ResultsActivity)
            adapter = this@ResultsActivity.adapter
        }
    }

    private fun openMaps(mapUrl: String) {
        if (mapUrl.isNotEmpty()) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Instala Google Maps para ver la ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadRestaurants() {
        var query: Query = db.collection("restaurants")

        with(filters) {
            if (department != "Seleccione") query = query.whereEqualTo("department", department)
            if (foodType != "Seleccione") query = query.whereEqualTo("foodType", foodType)
            if (promotionType != "Seleccione") query = query.whereEqualTo("promotionType", promotionType)
            if (environment != "Seleccione") query = query.whereEqualTo("environment", environment)
        }

        query.get()
            .addOnSuccessListener { documents ->
                val restaurants = documents.toObjects(Restaurant::class.java)
                adapter.submitList(restaurants)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar restaurantes: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}