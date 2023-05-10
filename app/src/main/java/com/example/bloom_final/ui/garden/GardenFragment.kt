package com.example.bloom_final.ui.garden

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.example.bloom_final.databinding.FragmentGardenBinding



class GardenFragment : Fragment() {



    private var _binding: FragmentGardenBinding? = null
    private lateinit var gardenAdapter: GardenAdapter
    private val plantList = mutableListOf<GardenPlants>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val gardenViewModel =
            ViewModelProvider(this).get(GardenViewModel::class.java)

        _binding = FragmentGardenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        gardenAdapter = GardenAdapter(plantList)
        binding.gardenRecyclerView.adapter = gardenAdapter

        setFragmentResultListener("plantIdentification") { _, result ->
            val plantName = result.getString("plant_name")
            val imageUri = result.getParcelable<Uri>("image_uri")
            if (plantName != null && imageUri != null) {
                plantList.add(GardenPlants(plantName, imageUri))
                gardenAdapter.notifyDataSetChanged()
            }
        }
        gardenViewModel.text.observe(viewLifecycleOwner) {
        }

        return root

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}