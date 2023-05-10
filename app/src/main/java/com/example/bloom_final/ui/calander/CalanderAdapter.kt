package com.example.bloom_final.ui.calander

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.bloom_final.R

class CalanderAdapter (private val context: Context, private val plantsMap: Map<String, List<String>>) :
    BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return plantsMap.size
    }

    override fun getItem(position: Int): Any {
        return plantsMap.keys.toTypedArray()[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.plant_list_item, parent, false)
        }

        val plantNameTextView = view!!.findViewById<TextView>(R.id.plantNameText)
        val feeddateTextView = view.findViewById<TextView>(R.id.feedDateText)
        val frequencyTextView = view!!.findViewById<TextView>(R.id.frequencyText)
        val locationTextView = view.findViewById<TextView>(R.id.probabilityText)
        val probabilityTextView = view.findViewById<TextView>(R.id.probabilityText)

        val plantName = plantsMap.keys.toTypedArray()[position]
        val probability = plantsMap[plantName]!![0]

        plantNameTextView.text = plantName
        probabilityTextView.text = probability

        return view
    }

}