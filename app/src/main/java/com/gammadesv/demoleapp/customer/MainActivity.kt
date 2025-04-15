package com.gammadesv.demoleapp.customer

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
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

        try {
            setupSpinners()
            setupButtons()
        } catch (e: Exception) {
            // Manejo de errores básico
            e.printStackTrace()
            finish() // Cierra la actividad si hay un error crítico
        }
    }

    private fun setupSpinners() {
        // Datos para los spinners
        val departments = resources.getStringArray(R.array.departments_array)
        val foodTypes = resources.getStringArray(R.array.food_types_array)
        val promoTypes = resources.getStringArray(R.array.promo_types_array)
        val environments = resources.getStringArray(R.array.environments_array)

        // Configuración segura de los adapters
        binding.spinnerDepartment.adapter = createSpinnerAdapter(departments)
        binding.spinnerFoodType.adapter = createSpinnerAdapter(foodTypes)
        binding.spinnerPromoType.adapter = createSpinnerAdapter(promoTypes)
        binding.spinnerEnvironment.adapter = createSpinnerAdapter(environments)
    }

    private fun createSpinnerAdapter(items: Array<String>): ArrayAdapter<String> {
        return ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            items
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun setupButtons() {
        binding.btnSearch.setOnClickListener {
            val filters = SearchFilters(
                department = binding.spinnerDepartment.selectedItem?.toString() ?: "",
                foodType = binding.spinnerFoodType.selectedItem?.toString() ?: "",
                promotionType = binding.spinnerPromoType.selectedItem?.toString() ?: "",
                environment = binding.spinnerEnvironment.selectedItem?.toString() ?: ""
            )

            if (validateFilters(filters)) {
                startActivity(
                    Intent(this, ResultsActivity::class.java).apply {
                        putExtra("filters", filters)
                    }
                )
            }
        }

        binding.btnRestaurant.setOnClickListener {
            startActivity(Intent(this, RestaurantAuthActivity::class.java))
        }
    }

    private fun validateFilters(filters: SearchFilters): Boolean {
        // Validación básica - todos los campos deben tener un valor seleccionado
        return listOf(
            filters.department,
            filters.foodType,
            filters.promotionType,
            filters.environment
        ).none { it == "Seleccione" || it.isEmpty() }
    }

    override fun onDestroy() {
        // Limpieza de recursos si es necesario
        super.onDestroy()
    }
}