package com.example.healthyme.UI.View.Favorit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.healthyme.Data.Model.Nutrition
import com.example.healthyme.databinding.FavMakananVertikalBinding

class FavoritAdapter(
    private val onBookmarkClick: (Nutrition) -> Unit // Menambahkan parameter untuk listener
) : ListAdapter<Nutrition, FavoritAdapter.FavoriteViewHolder>(NutritionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FavMakananVertikalBinding.inflate(inflater, parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val nutrition = getItem(position)
        holder.bind(nutrition)
    }

    inner class FavoriteViewHolder(private val binding: FavMakananVertikalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(nutrition: Nutrition) {
            binding.namaMakanan.text = nutrition.name
            binding.nilaiKalori.text = "${nutrition.calories} kcal"
            binding.nilaiProtein.text = "${nutrition.proteins} g"
            binding.nilaiFat.text = "${nutrition.fat} g"
            binding.nilaiKarbohidrat.text = "${nutrition.carbohydrate} g"

            // Load image using Glide
            Glide.with(binding.fotoMakanan.context)
                .load(nutrition.image)
                .centerCrop()
                .into(binding.fotoMakanan)

            // Set progress bar values
            val maxValue = 100 // Maximum value for the progress bars
            val proteinPercentage = (nutrition.proteins / maxValue * 100).toInt()
            val fatPercentage = (nutrition.fat / maxValue * 100).toInt()
            val carbPercentage = (nutrition.carbohydrate / maxValue * 100).toInt()

            // Set progress for each nutrient
            binding.progressProtein.progress = proteinPercentage
            binding.progressFat.progress = fatPercentage
            binding.progressKarbohidrat.progress = carbPercentage

            // Set OnClickListener untuk bookmark yang berfungsi untuk menghapus data
            binding.bookmark.setOnClickListener {
                onBookmarkClick(nutrition)  // Panggil fungsi untuk menghapus data
            }
        }
    }

    // DiffUtil callback untuk optimasi RecyclerView
    class NutritionDiffCallback : DiffUtil.ItemCallback<Nutrition>() {
        override fun areItemsTheSame(oldItem: Nutrition, newItem: Nutrition): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Nutrition, newItem: Nutrition): Boolean {
            return oldItem == newItem
        }
    }
}

