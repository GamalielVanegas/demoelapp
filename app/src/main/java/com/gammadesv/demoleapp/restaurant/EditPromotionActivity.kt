package com.gammadesv.demoleapp.restaurant

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gammadesv.demoleapp.databinding.ActivityEditPromotionBinding
import com.gammadesv.demoleapp.models.Promotion
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
                    with(binding) {
                        editTextTitle.setText(it.title)
                        editTextPromoType.setText(it.promotionType)
                        editTextDays.setText(it.days)
                        editTextHours.setText(it.hours)
                        editTextPrice.setText(it.price)
                    }
                }
            }
    }

    private fun setupButtons() {
        with(binding) {
            buttonUpdate.setOnClickListener { updatePromotion() }
            buttonDelete.setOnClickListener { deletePromotion() }
        }
    }

    private fun updatePromotion() {
        with(binding) {
            val title = editTextTitle.text.toString()
            val type = editTextPromoType.text.toString()
            val days = editTextDays.text.toString()
            val hours = editTextHours.text.toString()
            val price = editTextPrice.text.toString()

            if (title.isEmpty() || type.isEmpty()) {
                Toast.makeText(this@EditPromotionActivity, "Título y tipo son requeridos", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this@EditPromotionActivity, "Promoción actualizada", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@EditPromotionActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
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