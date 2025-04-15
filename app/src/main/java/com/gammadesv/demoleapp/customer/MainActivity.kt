package com.gammadesv.demoleapp.customer

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gammadesv.demoleapp.R
import com.gammadesv.demoleapp.databinding.ActivityMainBinding
import com.gammadesv.demoleapp.models.SearchFilters

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeUI()
    }

    private fun initializeUI() {
        try {
            setupSpinners()
            setupButtons()
        } catch (e: Exception) {
            handleInitializationError(e)
        }
    }

    private fun setupSpinners() {
        with(binding) {
            spinnerDepartment.adapter = createSpinnerAdapter(R.array.departments_array)
            spinnerFoodType.adapter = createSpinnerAdapter(R.array.food_types_array)
            spinnerPromoType.adapter = createSpinnerAdapter(R.array.promo_types_array)
            spinnerEnvironment.adapter = createSpinnerAdapter(R.array.environments_array)

            // Establecer selección por defecto
            arrayOf(spinnerDepartment, spinnerFoodType, spinnerPromoType, spinnerEnvironment)
                .forEach { it.setSelection(0, false) }
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
        binding.apply {
            btnSearch.setOnClickListener { handleSearchClick() }
            btnRestaurant.setOnClickListener { navigateToRestaurantAuth() }
        }
    }

    private fun handleSearchClick() {
        val filters = createSearchFilters()

        if (validateFilters(filters)) {
            navigateToResults(filters)
        } else {
            showValidationError()
        }
    }

    private fun createSearchFilters(): SearchFilters {
        return with(binding) {
            SearchFilters(
                department = spinnerDepartment.selectedItem?.toString().orEmpty(),
                foodType = spinnerFoodType.selectedItem?.toString().orEmpty(),
                promotionType = spinnerPromoType.selectedItem?.toString().orEmpty(),
                environment = spinnerEnvironment.selectedItem?.toString().orEmpty()
            )
        }
    }

    private fun validateFilters(filters: SearchFilters): Boolean {
        return with(filters) {
            department.isNotEmpty() && department != "Seleccione" &&
            foodType.isNotEmpty() && foodType != "Seleccione" &&
            promotionType.isNotEmpty() && promotionType != "Seleccione" &&
            environment.isNotEmpty() && environment != "Seleccione"
        }
    }

    private fun navigateToResults(filters: SearchFilters) {
        startActivity(
            Intent(this, ResultsActivity::class.java).apply {
                putExtra("filters", filters)
            }
        )
    }

    private fun navigateToRestaurantAuth() {
        startActivity(Intent(this, RestaurantAuthActivity::class.java))
    }

    private fun showValidationError() {
        Toast.makeText(
            this,
            "Por favor selecciona opciones válidas en todos los filtros",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun handleInitializationError(error: Exception) {
        error.printStackTrace()
        Toast.makeText(
            this,
            "Error al inicializar la aplicación. Por favor reinicia.",
            Toast.LENGTH_LONG
        ).show()
        finish()
    }

    override fun onDestroy() {
        // Limpieza de recursos
        binding.root.removeAllViews()
        super.onDestroy()
    }
}