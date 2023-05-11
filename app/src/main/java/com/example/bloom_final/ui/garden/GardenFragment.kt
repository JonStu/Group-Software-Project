package com.example.bloom_final.ui.garden

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bloom_final.R
import com.example.bloom_final.databinding.FragmentGardenBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GardenFragment : Fragment() {

    private var _binding: FragmentGardenBinding? = null
    private val binding get() = _binding!!

    private lateinit var plantAdapter: GardenAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var plantList: MutableList<GardenPlants>
    private lateinit var frequencySpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGardenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up RecyclerView with PlantAdapter
        plantList = loadPlantList()
        plantAdapter = GardenAdapter(plantList)
        val recyclerView: RecyclerView = binding.plantList
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = plantAdapter


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Listen for camera fragment results
        parentFragmentManager.setFragmentResultListener("plantIdentification", this) { _, result ->
            val plantName = result.getString("plant_name") ?: ""
            val probability = result.getString("probability") ?: ""

            // Add identified plant to plant list and save it
            val newPlant = GardenPlants(plantName, "", probability)
            plantList.add(newPlant)
            savePlantList(plantList)

            // Update PlantAdapter with new plant
            plantAdapter.notifyDataSetChanged()
        }
    }

    private fun loadPlantList(): MutableList<GardenPlants> {
        sharedPreferences = requireActivity().getSharedPreferences("PLANT_LIST", Context.MODE_PRIVATE)
        val plantListJson = sharedPreferences.getString("plant_list_json", "")
        return if (plantListJson.isNullOrEmpty()) {
            mutableListOf()
        } else {
            Gson().fromJson(plantListJson, object : TypeToken<MutableList<GardenPlants>>() {}.type)
        }
    }

    private fun savePlantList(plantList: MutableList<GardenPlants>) {
        val jsonString = Gson().toJson(plantList)
        sharedPreferences.edit().putString("plant_list_json", jsonString).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}