package com.example.bloom_final.ui.garden

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bloom_final.databinding.GardenviewitemBinding

class GardenAdapter(private val plantList: MutableList<GardenPlants>) : RecyclerView.Adapter<GardenAdapter.ViewHolder>() {

    private lateinit var frequencySpinner: Spinner

    class ViewHolder(val binding: GardenviewitemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GardenviewitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plant = plantList[position]
        holder.binding.plantName.text = plant.name
        holder.binding.plantProbability.text = "Probability: ${plant.probability}"

        // Load plant image if available
        if (plant.imageUri.isNotEmpty()) {
            Glide.with(holder.binding.root)
                .load(plant.imageUri)
                .into(holder.binding.plantImage)
        }
    }

    override fun getItemCount(): Int {
        return plantList.size
    }
}