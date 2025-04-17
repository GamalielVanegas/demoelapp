package com.gammadesv.demoleapp.adapters

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
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

            btnOpenMap.setOnClickListener {
                if (promotion.mapUrl.isNotEmpty()) {
                    openMapWithFallback(promotion.mapUrl)
                } else {
                    showToast("No hay mapa disponible para este lugar")
                }
            }

            itemView.findViewById<View>(R.id.btnEdit).setOnClickListener {
                if (showAdminActions) onEditClick(promotion)
            }

            itemView.findViewById<View>(R.id.btnDelete).setOnClickListener {
                if (showAdminActions) onDeleteClick(promotion)
            }
        }

        private fun openMapWithFallback(mapUrl: String) {
            try {
                Log.d("MAP_DEBUG", "Intentando abrir URL: $mapUrl")

                // Estrategia 1: Intentar con Google Maps directamente
                if (tryOpenWithGoogleMaps(mapUrl)) {
                    return
                }

                // Estrategia 2: Intentar con el esquema geo:
                if (tryOpenWithGeoUri(mapUrl)) {
                    return
                }

                // Estrategia 3: Intentar con navegador web
                if (tryOpenWithWebBrowser(mapUrl)) {
                    return
                }

                // Si todo falla
                showToast("No se pudo abrir el mapa. URL: ${shortenUrlForDisplay(mapUrl)}")

            } catch (e: Exception) {
                Log.e("MAP_ERROR", "Error al abrir mapa", e)
                showToast("Error al abrir el mapa: ${e.localizedMessage}")
            }
        }

        private fun tryOpenWithGoogleMaps(url: String): Boolean {
            return try {
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.google.android.apps.maps")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                if (intent.resolveActivity(itemView.context.packageManager) != null) {
                    itemView.context.startActivity(intent)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }

        private fun tryOpenWithGeoUri(url: String): Boolean {
            return try {
                // Extraer coordenadas si es una URL de Google Maps
                val coords = extractCoordinatesFromUrl(url)
                if (coords != null) {
                    val geoUri = Uri.parse("geo:$coords?q=$coords")
                    val intent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                        setPackage("com.google.android.apps.maps")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }

                    if (intent.resolveActivity(itemView.context.packageManager) != null) {
                        itemView.context.startActivity(intent)
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }

        private fun tryOpenWithWebBrowser(url: String): Boolean {
            return try {
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    setPackage(null) // Permitir que cualquier navegador lo maneje
                }

                if (intent.resolveActivity(itemView.context.packageManager) != null) {
                    itemView.context.startActivity(intent)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                false
            }
        }

        private fun extractCoordinatesFromUrl(url: String): String? {
            // Patrones comunes de URLs de Google Maps
            val patterns = listOf(
                "q=([-+]?\\d+\\.\\d+),([-+]?\\d+\\.\\d+)",  // ?q=lat,long
                "@(\\d+\\.\\d+),(\\d+\\.\\d+)"               // @lat,long
            )

            patterns.forEach { pattern ->
                val regex = Regex(pattern)
                val matchResult = regex.find(url)
                if (matchResult != null && matchResult.groupValues.size >= 3) {
                    return "${matchResult.groupValues[1]},${matchResult.groupValues[2]}"
                }
            }
            return null
        }

        private fun shortenUrlForDisplay(url: String): String {
            return if (url.length > 30) "${url.take(30)}..." else url
        }

        private fun showToast(message: String) {
            Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
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