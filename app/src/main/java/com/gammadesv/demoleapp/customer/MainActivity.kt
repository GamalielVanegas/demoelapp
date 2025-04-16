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
        // Configuración mejorada con tripletas (spinner, arrayRes, label)
        val spinnerConfigs = listOf(
            Triple(binding.spinnerDepartment, R.array.departments_array, getString(R.string.department_label)),
            Triple(binding.spinnerFoodType, R.array.food_types_array, getString(R.string.food_type_label)),
            Triple(binding.spinnerPromoType, R.array.promotion_types_array, getString(R.string.promo_type_label)),
            Triple(binding.spinnerEnvironment, R.array.environments_array, getString(R.string.environment_label))
        )

        spinnerConfigs.forEach { (spinner, arrayRes, label) ->
            // Configurar el adaptador
            spinner.adapter = createSpinnerAdapter(arrayRes)

            // Establecer el prompt (título cuando se abre el spinner)
            spinner.prompt = label

            // Seleccionar "Seleccione" por defecto
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

        if (filters.isValid(this)) {
            navigateToResults(filters)
        } else {
            showValidationError()
        }
    }

    private fun showValidationError() {
        Toast.makeText(
            this,
            getString(R.string.validation_error),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun navigateToResults(filters: SearchFilters) {
        Intent(this, ResultsActivity::class.java).apply {
            putExtra("search_filters", filters)
            startActivity(this)
        }
    }
}

// Extensión para validación (usando el string correcto)
fun SearchFilters.isValid(context: android.content.Context): Boolean {
    return listOf(department, foodType, promotionType, environment).all {
        it != context.getString(R.string.default_select_option) && it.isNotEmpty()
    }
}