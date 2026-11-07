package com.example.healthyme.UI.View.Home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.healthyme.databinding.SlideItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AutoSliderAdapter(
    private val images: List<Int>, private val viewPager: ViewPager2
) : RecyclerView.Adapter<AutoSliderAdapter.SliderViewHolder>() {
    private var currentPosition = 0

    init {
        startAutoSlide()
    }

    class SliderViewHolder(val binding: SlideItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = SlideItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
//        Picasso.get().load(images[position]).into(holder.binding.slideImage)
        holder.binding.slideImage.setImageResource(images[position])
    }

    private fun startAutoSlide() {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(3000)
                currentPosition = (currentPosition + 1) % itemCount
                viewPager.setCurrentItem(currentPosition, true)
            }
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }
}