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
    private val onEditClick: (Promotion) -> Unit,
    private val onDeleteClick: (Promotion) -> Unit
) : ListAdapter<Promotion, PromotionAdapter.PromotionViewHolder>(PromotionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_promotion, parent, false)
        return PromotionViewHolder(view, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PromotionViewHolder(
        itemView: View,
        private val onEditClick: (Promotion) -> Unit,
        private val onDeleteClick: (Promotion) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvPromoTitle)
        private val tvType: TextView = itemView.findViewById(R.id.tvPromoType)
        private val tvFoodType: TextView = itemView.findViewById(R.id.tvFoodType)
        private val tvSchedule: TextView = itemView.findViewById(R.id.tvPromoSchedule)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPromoPrice)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)

        fun bind(promotion: Promotion) {
            tvTitle.text = promotion.title
            tvType.text = promotion.promotionType
            tvFoodType.text = itemView.context.getString(R.string.food_type_prefix, promotion.foodType)
            tvSchedule.text = itemView.context.getString(R.string.promo_schedule_format, promotion.days, promotion.hours)
            tvPrice.text = itemView.context.getString(R.string.promo_price_format, promotion.price)
            tvLocation.text = itemView.context.getString(R.string.location_prefix, promotion.department)

            itemView.findViewById<View>(R.id.btnEdit).setOnClickListener {
                onEditClick(promotion)
            }

            itemView.findViewById<View>(R.id.btnDelete).setOnClickListener {
                onDeleteClick(promotion)
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