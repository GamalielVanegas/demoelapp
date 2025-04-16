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
import com.gammadesv.demoleapp.R

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
        val selectedFilters = listOf(
            filters.department.takeIf { it != getString(R.string.default_select_option) }
                ?.let { getString(R.string.location_prefix, it) },
            filters.foodType.takeIf { it != getString(R.string.default_select_option) }
                ?.let { getString(R.string.food_type_prefix, it) },
            filters.promotionType.takeIf { it != getString(R.string.default_select_option) },
            filters.environment.takeIf { it != getString(R.string.default_select_option) }
        ).filterNotNull()

        return if (selectedFilters.isNotEmpty()) {
            getString(R.string.your_selection, selectedFilters.joinToString(", "))
        } else {
            getString(R.string.all_restaurants)
        }
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
            val defaultOption = getString(R.string.default_select_option)
            if (department != defaultOption) query = query.whereEqualTo("department", department)
            if (foodType != defaultOption) query = query.whereEqualTo("foodType", foodType)
            if (promotionType != defaultOption) query = query.whereEqualTo("promotionType", promotionType)
            if (environment != defaultOption) query = query.whereEqualTo("environment", environment)
        }

        query.get()
            .addOnSuccessListener { documents ->
                val restaurants = documents.toObjects(Restaurant::class.java)
                adapter.submitList(restaurants)
                if (restaurants.isEmpty()) {
                    Toast.makeText(this, "No se encontraron restaurantes con esos filtros", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar restaurantes: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}