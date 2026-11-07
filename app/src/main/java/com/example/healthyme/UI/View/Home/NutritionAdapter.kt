package com.example.healthyme.UI.View.Home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.healthyme.Data.Model.Nutrition
import com.example.healthyme.databinding.HomeMakananVertikalBinding

class NutritionAdapter(
    private val nutritionList: List<Nutrition>,
    private val onItemClick: (Nutrition) -> Unit // Menambahkan parameter lambda untuk menangani klik
) : RecyclerView.Adapter<NutritionAdapter.NutritionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HomeMakananVertikalBinding.inflate(inflater, parent, false)
        return NutritionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NutritionViewHolder, position: Int) {
        val nutrition = nutritionList[position]
        holder.bind(nutrition)
    }

    inner class NutritionViewHolder(private val binding: HomeMakananVertikalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(nutrition: Nutrition) {
            binding.namaMakanan.text = nutrition.name
            binding.nilaiKalori.text = "${nutrition.calories} kcal"
            binding.nilaiProtein.text = "${nutrition.proteins} g"
            binding.nialiFat.text = "${nutrition.fat} g"
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

            // Set click listener to handle item clicks
            binding.root.setOnClickListener {
                onItemClick(nutrition) // Memanggil lambda saat item diklik
            }
        }
    }

    override fun getItemCount(): Int = nutritionList.size
}
