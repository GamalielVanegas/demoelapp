package com.gammadesv.demoleapp.customer

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.gammadesv.demoleapp.databinding.ActivityResultsBinding
import com.gammadesv.demoleapp.adapters.PromotionAdapter
import com.gammadesv.demoleapp.models.Promotion
import com.gammadesv.demoleapp.models.SearchFilters
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
            onEditClick = { promotion -> /* Manejar edición si es necesario */ },
            onDeleteClick = { promotion -> /* Manejar eliminación si es necesario */ }
        )

        binding.rvResults.apply {
            layoutManager = LinearLayoutManager(this@ResultsActivity)
            adapter = this@ResultsActivity.adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                setDrawable(ContextCompat.getDrawable(context, R.drawable.divider)!!)
                })
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

    private fun loadPromotions() {
        var query: Query = db.collection("promotions")

        with(filters) {
            val defaultOption = getString(R.string.default_select_option)
            if (department != defaultOption) query = query.whereEqualTo("department", department)
            if (foodType != defaultOption) query = query.whereEqualTo("foodType", foodType)
            if (promotionType != defaultOption) query = query.whereEqualTo("promotionType", promotionType)
            if (environment != defaultOption) query = query.whereEqualTo("environment", environment)
        }

        query.get()
            .addOnSuccessListener { documents ->
                val promotions = documents.toObjects(Promotion::class.java)
                adapter.submitList(promotions)
                if (promotions.isEmpty()) {
                    Toast.makeText(this, "No se encontraron promociones con esos filtros", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar promociones: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}