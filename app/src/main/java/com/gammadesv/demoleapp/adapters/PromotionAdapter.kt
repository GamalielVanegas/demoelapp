package com.gammadesv.demoleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        private val tvPromoType: TextView = itemView.findViewById(R.id.tvPromoType)
        private val tvFoodType: TextView = itemView.findViewById(R.id.tvFoodType)
        private val tvPromoSchedule: TextView = itemView.findViewById(R.id.tvPromoSchedule)
        private val tvPromoPrice: TextView = itemView.findViewById(R.id.tvPromoPrice)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        private val tvRestaurantAddress: TextView = itemView.findViewById(R.id.tvRestaurantAddress)
        private val adminActions: View = itemView.findViewById(R.id.adminActions)

        fun bind(promotion: Promotion) {
            tvRestaurantName.text = promotion.title
            tvPromoType.text = promotion.promotionType
            tvFoodType.text = promotion.foodType.toLowerCase().capitalize()
            tvPromoSchedule.text = "${promotion.days}, ${promotion.hours}"
            tvPromoPrice.text = "$${promotion.price}"
            tvLocation.text = "En ${promotion.department}"
            // Versión segura con manejo de nulos

            adminActions.visibility = if (showAdminActions) View.VISIBLE else View.GONE

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