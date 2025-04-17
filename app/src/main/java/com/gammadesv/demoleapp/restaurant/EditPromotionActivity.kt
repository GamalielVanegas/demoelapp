package com.gammadesv.demoleapp.restaurant

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gammadesv.demoleapp.R
import com.gammadesv.demoleapp.databinding.ActivityEditPromotionBinding
import com.gammadesv.demoleapp.models.Promotion
import com.google.firebase.firestore.FirebaseFirestore

class EditPromotionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPromotionBinding
    private lateinit var db: FirebaseFirestore
    private var promotionId: String = ""

    // Variables para guardar selecciones
    private var selectedDepartment: String = ""
    private var selectedFoodType: String = ""
    private var selectedPromotionType: String = ""
    private var selectedEnvironment: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPromotionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        promotionId = intent.getStringExtra("promotion_id") ?: ""

        if (promotionId.isEmpty()) {
            finish()
            return
        }

        setupSpinners()
        loadPromotion()
        setupButtons()
    }

    private fun setupSpinners() {
        // Configuración Spinner de Departamento
        setupSpinner(
            spinner = binding.spinnerDepartment,
            arrayRes = R.array.departments_array,
            onItemSelected = { selectedDepartment = it }
        )

        // Configuración Spinner de Tipo de Comida
        setupSpinner(
            spinner = binding.spinnerFoodType,
            arrayRes = R.array.food_types_array,
            onItemSelected = { selectedFoodType = it }
        )

        // Configuración Spinner de Tipo de Promoción
        setupSpinner(
            spinner = binding.spinnerPromoType,
            arrayRes = R.array.promotion_types_array,
            onItemSelected = { selectedPromotionType = it }
        )

        // Configuración Spinner de Ambiente
        setupSpinner(
            spinner = binding.spinnerEnvironment,
            arrayRes = R.array.environments_array,
            onItemSelected = { selectedEnvironment = it }
        )
    }

    private fun setupSpinner(
        spinner: Spinner,
        arrayRes: Int,
        onItemSelected: (String) -> Unit
    ) {
        ArrayAdapter.createFromResource(
            this,
            arrayRes,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selected = parent?.getItemAtPosition(position).toString()
                    onItemSelected(selected)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun loadPromotion() {
        db.collection("promotions").document(promotionId)
            .get()
            .addOnSuccessListener { document ->
                val promotion = document.toObject(Promotion::class.java)
                promotion?.let {
                    with(binding) {
                        editTextTitle.setText(it.title)
                        setSpinnerSelection(spinnerDepartment, it.department)
                        setSpinnerSelection(spinnerFoodType, it.foodType)
                        setSpinnerSelection(spinnerPromoType, it.promotionType)
                        setSpinnerSelection(spinnerEnvironment, it.environment)
                        editTextDays.setText(it.days)
                        editTextHours.setText(it.hours)
                        editTextPrice.setText(it.price)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar promoción: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun setSpinnerSelection(spinner: Spinner, value: String) {
        val adapter = spinner.adapter as ArrayAdapter<*>
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).toString() == value) {
                spinner.setSelection(i)
                return
            }
        }
        // Si no encuentra el valor, establecer la selección en 0 (Seleccione)
        spinner.setSelection(0)
    }

    private fun setupButtons() {
        with(binding) {
            buttonUpdate.setOnClickListener { updatePromotion() }
            buttonDelete.setOnClickListener { deletePromotion() }
        }
    }

    private fun updatePromotion() {
        with(binding) {
            val title = editTextTitle.text.toString().trim()
            val days = editTextDays.text.toString().trim()
            val hours = editTextHours.text.toString().trim()
            val price = editTextPrice.text.toString().trim()

            // Validaciones
            if (title.isEmpty() || selectedPromotionType == getString(R.string.default_select_option)) {
                Toast.makeText(this@EditPromotionActivity, "Título y tipo de promoción son requeridos", Toast.LENGTH_SHORT).show()
                return
            }

            if (selectedDepartment == getString(R.string.default_select_option) ||
                selectedFoodType == getString(R.string.default_select_option) ||
                selectedEnvironment == getString(R.string.default_select_option)) {
                Toast.makeText(this@EditPromotionActivity, "Por favor seleccione todas las opciones", Toast.LENGTH_SHORT).show()
                return
            }

            val updates = mapOf(
                "title" to title,
                "department" to selectedDepartment,
                "foodType" to selectedFoodType,
                "promotionType" to selectedPromotionType,
                "environment" to selectedEnvironment,
                "days" to days,
                "hours" to hours,
                "price" to price
            )

            db.collection("promotions").document(promotionId)
                .update(updates)
                .addOnSuccessListener {
                    Toast.makeText(
                        this@EditPromotionActivity,
                        "Promoción actualizada con éxito",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@EditPromotionActivity,
                        "Error al actualizar: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun deletePromotion() {
        db.collection("promotions").document(promotionId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Promoción eliminada con éxito",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al eliminar: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}