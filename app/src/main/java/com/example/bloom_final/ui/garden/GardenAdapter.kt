package com.example.bloom_final.ui.garden

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bloom_final.databinding.GardenviewitemBinding

class GardenAdapter(private val plantList: List<GardenPlants>) : RecyclerView.Adapter<GardenAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GardenviewitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPlant = plantList[position]
        holder.bind(currentPlant)
    }

    override fun getItemCount(): Int {
        return plantList.size
    }

    class ViewHolder(private val binding: GardenviewitemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: GardenPlants) {
            binding.apply {
                gardenPlant.text = plant.plantName
                Glide.with(gardenPlant)
                    .load(plant.imageUri)
                    .centerCrop()
                    .into(plantPicture)
            }
        }
    }
}
