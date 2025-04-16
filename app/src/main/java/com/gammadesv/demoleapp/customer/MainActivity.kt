package com.gammadesv.demoleapp.customer

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.appcompat.app.AppCompatActivity
import com.gammadesv.demoleapp.R
import com.gammadesv.demoleapp.databinding.ActivityMainBinding
import com.gammadesv.demoleapp.models.SearchFilters
import com.gammadesv.demoleapp.restaurant.auth.RestaurantAuthActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeUI()
    }

    private fun initializeUI() {
        setupSpinners()
        setupButtons()
    }

    private fun setupSpinners() {
        listOf(
            binding.spinnerDepartment to R.array.departments_array,
            binding.spinnerFoodType to R.array.food_types_array,
            binding.spinnerPromoType to R.array.promo_types_array,
            binding.spinnerEnvironment to R.array.environments_array
        ).forEach { (spinner, arrayRes) ->
            spinner.adapter = createSpinnerAdapter(arrayRes)
            spinner.setSelection(0, false)
        }
    }

    private fun createSpinnerAdapter(@ArrayRes arrayResId: Int): ArrayAdapter<CharSequence> {
        return ArrayAdapter.createFromResource(
            this,
            arrayResId,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun setupButtons() {
        binding.btnSearch.setOnClickListener { handleSearch() }
        binding.btnRestaurant.setOnClickListener {
            startActivity(Intent(this, RestaurantAuthActivity::class.java))
        }
    }

    private fun handleSearch() {
        val filters = SearchFilters(
            department = binding.spinnerDepartment.selectedItem.toString(),
            foodType = binding.spinnerFoodType.selectedItem.toString(),
            promotionType = binding.spinnerPromoType.selectedItem.toString(),
            environment = binding.spinnerEnvironment.selectedItem.toString()
        )

        if (filters.isValid()) {
            navigateToResults(filters)
        } else {
            Toast.makeText(
                this,
                "Selecciona opciones válidas en todos los filtros",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun navigateToResults(filters: SearchFilters) {
        Intent(this, ResultsActivity::class.java).apply {
            putExtra("search_filters", filters)
            startActivity(this)
        }
    }
}

// Extensión para validación
fun SearchFilters.isValid(): Boolean {
    return listOf(department, foodType, promotionType, environment).all {
        it != "Seleccione" && it.isNotEmpty()
    }
}