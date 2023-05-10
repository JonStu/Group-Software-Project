package com.example.bloom_final.ui.calander

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bloom_final.R
import com.example.bloom_final.databinding.FragmentCalanderBinding
import java.util.Locale

class CalanderFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var plantsList: ListView

    private var _binding: FragmentCalanderBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val calanderViewModel = ViewModelProvider(this).get(CalanderViewModel::class.java)

        _binding = FragmentCalanderBinding.inflate(inflater, container, false)
        val root: View = binding.root

        calendarView = binding.calendarView
        plantsList = binding.plantsList

        // Set up the adapter for the listview
        val adapter = CalanderAdapter(requireContext(), mapOf())
        plantsList.adapter = adapter

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = getDate(year, month, dayOfMonth)
            updatePlantsList(date)
        }

        return root
    }

    private fun getDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return simpleDateFormat.format(calendar.time)
    }

    private fun updatePlantsList(date: String) {
        // Retrieve information from bundle
        val plantName = arguments?.getString("plant_name")
        val probability = arguments?.getString("probability")

        if (plantName != null && probability != null) {
            // Get current list of plants for the selected date
            val plantsMap = getPlantsMap(date)

            // Add the new plant to the list for the selected date
            val newList = plantsMap.toMutableMap()
            if (newList.containsKey(plantName)) {
                val currentProbabilityList = newList[plantName]!!.toMutableList()
                currentProbabilityList.add(probability)
                newList[plantName] = currentProbabilityList.toList()
            } else {
                newList[plantName] = listOf(probability)
            }

            // Update the adapter with the new list of plants
            val adapter = CalanderAdapter(requireContext(), newList)
            plantsList.adapter = adapter
        }
    }

    private fun getPlantsMap(date: String): Map<String, List<String>> {
        // Get the current map of plants for the selected date from a database or other storage mechanism.
        // For the purpose of this example, we will just return an empty map.
        return mapOf()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}