package com.example.wetherapp.controler.controler.adapters

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wetherapp.R
import com.example.wetherapp.databinding.ItemCurrentBinding
import com.example.wetherapp.model.WeatherModel
import com.example.wetherapp.view.MainActivity
import com.example.wetherapp.view.WeatherRadar


class CurrentAdapter : ListAdapter<WeatherModel, CurrentAdapter.Holder>(Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemCurrentBinding.bind(view)


        fun bind(item: WeatherModel) = with(binding) {
            val wind = "${String.format("%.0f", item.vWind.toDouble())} khm"
            val pressure = "${String.format("%.0f",item.pressure.toDouble())} Pa"
            val humidity = "${item.humidity}%"
            val cloud = "${item.cloud}%"
            countryValue.text = item.country
            windKhmValue.text = wind
            pressureValue.text = pressure
            humidityValue.text = humidity
            cloudValue.text = cloud
            windDirValue.text = item.dirWind
            sunriseValue.text = item.sunrise
            sunsetValue.text = item.sunset
            moonPhaseValue.text = item.moonPhase
        }
    }

    class Comparator : DiffUtil.ItemCallback<WeatherModel>() {
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_current, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

}