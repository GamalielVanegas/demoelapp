package com.gammadesv.demoleapp.customer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.gammadesv.demoleapp.adapters.PromotionAdapter
import com.gammadesv.demoleapp.databinding.ActivityResultsBinding
import com.gammadesv.demoleapp.models.Promotion
import com.gammadesv.demoleapp.models.SearchFilters
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.widget.Toast
import com.google.firebase.firestore.Query
import com.gammadesv.demoleapp.R

class ResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultsBinding
    private lateinit var adapter: PromotionAdapter
    private lateinit var filters: SearchFilters

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filters = intent.getParcelableExtra("search_filters") ?: SearchFilters()
        setupUI()
        loadPromotions()
    }

    private fun setupUI() {
        binding.tvSelection.text = buildSelectionTitle()
        binding.tvSelection.setTextColor(ContextCompat.getColor(this, R.color.primary_orange))
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
        adapter = PromotionAdapter(
            onEditClick = { /* No action for normal users */ },
            onDeleteClick = { /* No action for normal users */ },
            showAdminActions = false
        )

        binding.rvResults.apply {
            layoutManager = LinearLayoutManager(this@ResultsActivity)
            adapter = this@ResultsActivity.adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context, R.drawable.divider)!!)
            })
        }
    }

    private fun loadPromotions() {
        var query: Query = db.collection("promotions")
            .whereEqualTo("isActive", true)

        with(filters) {
            val defaultOption = getString(R.string.default_select_option)
            if (department != defaultOption) query = query.whereEqualTo("department", department)
            if (foodType != defaultOption) query = query.whereEqualTo("foodType", foodType)
            if (promotionType != defaultOption) query = query.whereEqualTo("promotionType", promotionType)
            if (environment != defaultOption) query = query.whereEqualTo("environment", environment)
        }

        query.get()
            .addOnSuccessListener { documents ->
                val promotions = mutableListOf<Promotion>()
                val restaurantIds = documents.map { it.getString("restaurantId") }.distinct()

                if (restaurantIds.isEmpty()) {
                    adapter.submitList(emptyList())
                    return@addOnSuccessListener
                }

                db.collection("restaurants")
                    .whereIn(FieldPath.documentId(), restaurantIds)
                    .get()
                    .addOnSuccessListener { restaurantDocs ->
                        val restaurants = restaurantDocs.associateBy { it.id }

                        documents.forEach { doc ->
                            val promotionData = doc.data
                            val isActive = promotionData["isActive"] as? Boolean ?:
                            (promotionData["active"] as? Boolean ?: true)

                            if (isActive) {
                                val restaurant = restaurants[promotionData["restaurantId"] as? String]
                                val promotion = Promotion(
                                    id = doc.id,
                                    restaurantId = promotionData["restaurantId"] as? String ?: "",
                                    restaurantName = restaurant?.getString("name") ?: "",
                                    restaurantAddress = restaurant?.getString("address") ?: "",
                                    title = promotionData["title"] as? String ?: "",
                                    promotionType = promotionData["promotionType"] as? String ?: "",
                                    days = promotionData["days"] as? String ?: "",
                                    hours = promotionData["hours"] as? String ?: "",
                                    price = promotionData["price"] as? String ?: "",
                                    department = promotionData["department"] as? String ?: "",
                                    foodType = promotionData["foodType"] as? String ?: "",
                                    environment = promotionData["environment"] as? String ?: "",
                                    isActive = isActive,
                                    mapUrl = restaurant?.getString("mapUrl") ?: "",
                                    createdAt = promotionData["createdAt"] as? Long ?: 0L
                                )
                                promotions.add(promotion)
                            }
                        }

                        adapter.submitList(promotions)
                        if (promotions.isEmpty()) {
                            Toast.makeText(this, "No se encontraron promociones activas", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al cargar restaurantes: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar promociones: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}