package com.gammadesv.demoleapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gammadesv.demoleapp.databinding.ItemRestaurantBinding
import com.gammadesv.demoleapp.models.Restaurant

class RestaurantAdapter(
    private val onItemClick: (Restaurant) -> Unit
) : ListAdapter<Restaurant, RestaurantAdapter.RestaurantViewHolder>(RestaurantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RestaurantViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RestaurantViewHolder(
        private val binding: ItemRestaurantBinding,
        private val onItemClick: (Restaurant) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(restaurant: Restaurant) {
            with(binding) {
                tvRestaurantName.text = restaurant.name
                tvRestaurantAddress.text = restaurant.address
                //tvPromotion.text = restaurant.promotionTitle
                //tvPrice.text = restaurant.promotionPrice
                tvPhone.text = restaurant.phone

                root.setOnClickListener { onItemClick(restaurant) }
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