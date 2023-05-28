package com.example.wetherapp.controler.controler.adapters

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherapp.R
import com.example.wetherapp.databinding.ItemAirBinding
import com.example.wetherapp.databinding.ItemCurrentBinding
import com.example.wetherapp.model.AirQualityModel
import com.example.wetherapp.model.WeatherModel
import com.example.wetherapp.view.MainActivity
import com.example.wetherapp.view.WeatherRadar
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter


class AirQualityAdapter : ListAdapter<AirQualityModel, AirQualityAdapter.Holder>(Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemAirBinding.bind(view)


        fun bind(item: AirQualityModel) = with(binding) {
            val usEpaiIndex = item.usEpaIndex.toInt()
            val cardio = item.co.toFloat()
            val nitdio = item.no2.toFloat()
            val ozone = item.o3.toFloat()
            val suldio = item.so2.toFloat()
            val pm10 = item.pm10.toFloat()
            val pm25 = item.pm2_5.toFloat()
            val entries = listOf(
                PieEntry(cardio, "Carbon dioxide"),
                PieEntry(nitdio, "Nitrogen dioxide"),
                PieEntry(ozone, "Ozone"),
                PieEntry(suldio, "Sulfur dioxide"),
                PieEntry(pm10, "PM10"),
                PieEntry(pm25, "PM2.5")
            )
            val mapIndex = mapOf(
                1 to "Good",
                2 to "Moderate",
                3 to "Unhealthy for sensitive group",
                4 to "Unhealthy",
                5 to "Very Unhealthy",
                6 to "Hazardous"
            )
            val dataSet = PieDataSet(entries, "")
            dataSet.colors =
                listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.BLACK, Color.MAGENTA)
            dataSet.sliceSpace = 3f
            dataSet.valueTextSize = 12f
            val pieData = PieData(dataSet)
            pieData.setValueTextSize(16f)
            pieData.setValueTextColor(Color.WHITE)
            pieData.setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()} μg/m3"
                }
            })
            chart.data = pieData
            chart.isRotationEnabled = true
            chart.distanceToCenter(25f, 25f)
            chart.description.textColor = Color.WHITE
            chart.description.text = "Air Quality"
            chart.description.textSize = 13f
            chart.setDrawEntryLabels(false)
            chart.invalidate()

            val legend = binding.chart.legend
            legend.textColor = Color.WHITE
            legend.textSize = 16f
            legend.isWordWrapEnabled = true
            val textAirQ = "Air Quality by US - EPA standard: ${mapIndex[usEpaiIndex]}"
            binding.textView3.text = textAirQ
        }
    }


    class Comparator : DiffUtil.ItemCallback<AirQualityModel>() {
        override fun areItemsTheSame(oldItem: AirQualityModel, newItem: AirQualityModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: AirQualityModel,
            newItem: AirQualityModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_air, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

}