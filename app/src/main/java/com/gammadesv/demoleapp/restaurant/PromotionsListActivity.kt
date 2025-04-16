package com.gammadesv.demoleapp.restaurant

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gammadesv.demoleapp.adapters.PromotionAdapter
import com.gammadesv.demoleapp.databinding.ActivityPromotionsListBinding
import com.gammadesv.demoleapp.models.Promotion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PromotionsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPromotionsListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: PromotionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPromotionsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        loadPromotions()
    }

    private fun setupRecyclerView() {
        adapter = PromotionAdapter(
            onEditClick = { promotion ->
                startActivity(Intent(this, EditPromotionActivity::class.java).apply {
                    putExtra("promotion_id", promotion.id)
                })
            },
            onDeleteClick = { promotion ->
                deletePromotion(promotion.id)
            }
        )

        with(binding) {
            recyclerViewPromotions.layoutManager = LinearLayoutManager(this@PromotionsListActivity)
            recyclerViewPromotions.adapter = adapter
        }
    }

    private fun loadPromotions() {
        val restaurantId = auth.currentUser?.uid ?: return

        db.collection("promotions")
            .whereEqualTo("restaurantId", restaurantId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) return@addSnapshotListener

                val promotions = mutableListOf<Promotion>()
                snapshots?.forEach { document ->
                    promotions.add(document.toObject(Promotion::class.java).copy(id = document.id))
                }
                adapter.submitList(promotions)
            }
    }

    private fun deletePromotion(promotionId: String) {
        db.collection("promotions").document(promotionId)
            .delete()
            .addOnSuccessListener {
                // La eliminación se reflejará automáticamente en el listener
            }
    }
}