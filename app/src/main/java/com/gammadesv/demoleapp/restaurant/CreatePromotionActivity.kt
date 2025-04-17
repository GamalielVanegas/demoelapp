package com.gammadesv.demoleapp.restaurant

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gammadesv.demoleapp.R
import com.gammadesv.demoleapp.databinding.ActivityCreatePromotionBinding
import com.gammadesv.demoleapp.models.Promotion
import com.gammadesv.demoleapp.models.Restaurant // Importación añadida
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class CreatePromotionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePromotionBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var selectedDepartment: String = ""
    private var selectedFoodType: String = ""
    private var selectedPromotionType: String = ""
    private var selectedEnvironment: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePromotionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupSpinners()
        binding.btnSavePromo.setOnClickListener { savePromotion() }
    }

    private fun setupSpinners() {
        // Configuración Spinner de Departamento
        ArrayAdapter.createFromResource(
            this,
            R.array.departments_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDepartment.adapter = adapter
            binding.spinnerDepartment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedDepartment = parent?.getItemAtPosition(position).toString()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        // Configuración Spinner de Tipo de Comida
        ArrayAdapter.createFromResource(
            this,
            R.array.food_types_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerFoodType.adapter = adapter
            binding.spinnerFoodType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedFoodType = parent?.getItemAtPosition(position).toString()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        // Configuración Spinner de Tipo de Promoción
        ArrayAdapter.createFromResource(
            this,
            R.array.promotion_types_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerPromoType.adapter = adapter
            binding.spinnerPromoType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedPromotionType = parent?.getItemAtPosition(position).toString()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        // Configuración Spinner de Ambiente
        ArrayAdapter.createFromResource(
            this,
            R.array.environments_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerEnvironment.adapter = adapter
            binding.spinnerEnvironment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedEnvironment = parent?.getItemAtPosition(position).toString()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun savePromotion() {
        val restaurantId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val title = binding.etTitle.text.toString().trim()
        val days = binding.etDays.text.toString().trim()
        val hours = binding.etHours.text.toString().trim()
        val price = binding.etPrice.text.toString().trim()

        if (title.isEmpty() || selectedPromotionType == getString(R.string.default_select_option)) {
            Toast.makeText(this, "Título y tipo de promoción son requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("restaurants").document(restaurantId).get()
            .addOnSuccessListener { document ->
                val restaurant = document.toObject(Restaurant::class.java) ?: run {
                    Toast.makeText(this, "Restaurante no encontrado", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val promotion = Promotion(
                    id = UUID.randomUUID().toString(),
                    restaurantId = restaurantId,
                    restaurantName = restaurant.name,
                    restaurantAddress = restaurant.address,
                    title = title,
                    promotionType = selectedPromotionType,
                    department = selectedDepartment,
                    foodType = selectedFoodType,
                    environment = selectedEnvironment,
                    days = days,
                    hours = hours,
                    price = price,
                    createdAt = System.currentTimeMillis()
                )

                db.collection("promotions").add(promotion)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Promoción creada exitosamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al crear promoción: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener datos del restaurante: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }
}