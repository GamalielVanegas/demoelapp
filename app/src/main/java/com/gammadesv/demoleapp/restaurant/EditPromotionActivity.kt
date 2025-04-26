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
    private var currentPromotion: Promotion? = null

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
        setupSpinner(
            spinner = binding.spinnerDepartment,
            arrayRes = R.array.departments_array,
            onItemSelected = { selectedDepartment = it }
        )

        setupSpinner(
            spinner = binding.spinnerFoodType,
            arrayRes = R.array.food_types_array,
            onItemSelected = { selectedFoodType = it }
        )

        setupSpinner(
            spinner = binding.spinnerPromoType,
            arrayRes = R.array.promotion_types_array,
            onItemSelected = { selectedPromotionType = it }
        )

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
                currentPromotion = document.toObject(Promotion::class.java)
                currentPromotion?.let { promotion ->
                    with(binding) {
                        editTextTitle.setText(promotion.title)
                        setSpinnerSelection(spinnerDepartment, promotion.department)
                        setSpinnerSelection(spinnerFoodType, promotion.foodType)
                        setSpinnerSelection(spinnerPromoType, promotion.promotionType)
                        setSpinnerSelection(spinnerEnvironment, promotion.environment)
                        editTextDays.setText(promotion.days)
                        editTextHours.setText(promotion.hours)
                        editTextPrice.setText(promotion.price)
                        switchActive.isChecked = promotion.isActive
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
            val isActive = switchActive.isChecked

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

            // Crear mapa de actualizaciones
            val updates = hashMapOf<String, Any>(
                "title" to title,
                "department" to selectedDepartment,
                "foodType" to selectedFoodType,
                "promotionType" to selectedPromotionType,
                "environment" to selectedEnvironment,
                "days" to days,
                "hours" to hours,
                "price" to price,
                "isActive" to isActive
            )

            // Agregar campo 'active' solo si existe en el documento original
            currentPromotion?.let {
                if (it.isActive != isActive) { // Solo si cambió el estado
                    updates["active"] = isActive
                }
            }

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