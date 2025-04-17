package com.gammadesv.demoleapp.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gammadesv.demoleapp.R
import com.gammadesv.demoleapp.models.Promotion

class PromotionAdapter(
    private val onEditClick: (Promotion) -> Unit = {},
    private val onDeleteClick: (Promotion) -> Unit = {},
    private val showAdminActions: Boolean = false
) : ListAdapter<Promotion, PromotionAdapter.PromotionViewHolder>(PromotionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promotion, parent, false)
        return PromotionViewHolder(view, onEditClick, onDeleteClick, showAdminActions)
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PromotionViewHolder(
        itemView: View,
        private val onEditClick: (Promotion) -> Unit,
        private val onDeleteClick: (Promotion) -> Unit,
        private val showAdminActions: Boolean
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvRestaurantName: TextView = itemView.findViewById(R.id.tvRestaurantName)
        private val tvPromoTitle: TextView = itemView.findViewById(R.id.tvPromoTitle)
        private val tvPromoType: TextView = itemView.findViewById(R.id.tvPromoType)
        private val tvFoodType: TextView = itemView.findViewById(R.id.tvFoodType)
        private val tvPromoSchedule: TextView = itemView.findViewById(R.id.tvPromoSchedule)
        private val tvPromoPrice: TextView = itemView.findViewById(R.id.tvPromoPrice)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        private val tvRestaurantAddress: TextView = itemView.findViewById(R.id.tvRestaurantAddress)
        private val adminActions: View = itemView.findViewById(R.id.adminActions)
        private val btnOpenMap: Button = itemView.findViewById(R.id.btnOpenMap)

        fun bind(promotion: Promotion) {
            tvRestaurantName.text = promotion.restaurantName
            tvPromoTitle.text = promotion.title
            tvPromoType.text = promotion.promotionType
            tvFoodType.text = promotion.foodType.toLowerCase().capitalize()
            tvPromoSchedule.text = "${promotion.days}, ${promotion.hours}"
            tvPromoPrice.text = "$${promotion.price}"
            tvLocation.text = "En ${promotion.department}"
            tvRestaurantAddress.text = promotion.restaurantAddress

            adminActions.visibility = if (showAdminActions) View.VISIBLE else View.GONE

            // Configurar el botón del mapa
            btnOpenMap.setOnClickListener {
                if (promotion.mapUrl.isNotEmpty()) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(promotion.mapUrl))
                        intent.setPackage("com.google.android.apps.maps")
                        itemView.context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            itemView.context,
                            "Instala Google Maps para ver la ubicación",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        itemView.context,
                        "No hay mapa disponible para este lugar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            itemView.findViewById<View>(R.id.btnEdit).setOnClickListener {
                if (showAdminActions) onEditClick(promotion)
            }

            itemView.findViewById<View>(R.id.btnDelete).setOnClickListener {
                if (showAdminActions) onDeleteClick(promotion)
            }
        }
    }
}

class PromotionDiffCallback : DiffUtil.ItemCallback<Promotion>() {
    override fun areItemsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
        return oldItem == newItem
    }
}