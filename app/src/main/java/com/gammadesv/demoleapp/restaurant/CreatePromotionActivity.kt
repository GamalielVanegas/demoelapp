package com.gammadesv.demoleapp.restaurant

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gammadesv.demoleapp.databinding.ActivityCreatePromotionBinding
import com.gammadesv.demoleapp.models.Promotion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreatePromotionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePromotionBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePromotionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnSavePromo.setOnClickListener { savePromotion() }
    }

    private fun savePromotion() {
        val restaurantId = auth.currentUser?.uid ?: return
        val promotionType = binding.etPromoType.text.toString()
        val days = binding.etDays.text.toString()
        val hours = binding.etHours.text.toString()
        val price = binding.etPrice.text.toString()
        val title = binding.etTitle.text.toString()

        if (promotionType.isEmpty() || title.isEmpty()) {
            Toast.makeText(this, "Tipo y título son requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        val promotion = Promotion(
            restaurantId = restaurantId,
            promotionType = promotionType,
            days = days,
            hours = hours,
            price = price,
            title = title
        )

        db.collection("promotions").add(promotion)
            .addOnSuccessListener {
                Toast.makeText(this, "Promoción creada", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}