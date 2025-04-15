package com.gammadesv.demoleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gammadesv.demoleapp.R
import com.gammadesv.demoleapp.models.Restaurant

class RestaurantAdapter(
    private val onItemClick: (Restaurant) -> Unit
) : ListAdapter<Restaurant, RestaurantAdapter.RestaurantViewHolder>(RestaurantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RestaurantViewHolder(
        itemView: View,
        private val onItemClick: (Restaurant) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvRestaurantName)
        private val tvPromo: TextView = itemView.findViewById(R.id.tvPromotion)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)

        fun bind(restaurant: Restaurant) {
            tvName.text = restaurant.name
            tvPromo.text = restaurant.promotionTitle
            tvPrice.text = restaurant.promotionPrice
            tvPhone.text = restaurant.phone

            itemView.setOnClickListener {
                onItemClick(restaurant)
            }
        }
    }
}

class RestaurantDiffCallback : DiffUtil.ItemCallback<Restaurant>() {
    override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem == newItem
    }
}