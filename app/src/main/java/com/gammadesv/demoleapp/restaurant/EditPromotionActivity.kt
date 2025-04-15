package com.gammadesv.demoleapp.restaurant

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gammadesv.demoleapp.databinding.ActivityEditPromotionBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditPromotionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPromotionBinding
    private lateinit var db: FirebaseFirestore
    private var promotionId: String = ""

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

        loadPromotion()
        setupButtons()
    }

    private fun loadPromotion() {
        db.collection("promotions").document(promotionId)
            .get()
            .addOnSuccessListener { document ->
                val promotion = document.toObject(Promotion::class.java)
                promotion?.let {
                    binding.etTitle.setText(it.title)
                    binding.etPromoType.setText(it.promotionType)
                    binding.etDays.setText(it.days)
                    binding.etHours.setText(it.hours)
                    binding.etPrice.setText(it.price)
                }
            }
    }

    private fun setupButtons() {
        binding.btnUpdate.setOnClickListener { updatePromotion() }
        binding.btnDelete.setOnClickListener { deletePromotion() }
    }

    private fun updatePromotion() {
        val title = binding.etTitle.text.toString()
        val type = binding.etPromoType.text.toString()
        val days = binding.etDays.text.toString()
        val hours = binding.etHours.text.toString()
        val price = binding.etPrice.text.toString()

        if (title.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Título y tipo son requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = mapOf(
            "title" to title,
            "promotionType" to type,
            "days" to days,
            "hours" to hours,
            "price" to price
        )

        db.collection("promotions").document(promotionId)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Promoción actualizada", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deletePromotion() {
        db.collection("promotions").document(promotionId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Promoción eliminada", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}