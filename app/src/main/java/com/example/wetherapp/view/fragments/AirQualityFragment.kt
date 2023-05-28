package com.example.wetherapp.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetherapp.controler.controler.adapters.AirQualityAdapter
import com.example.wetherapp.model.MainViewModel
import com.example.wetherapp.controler.controler.adapters.CurrentAdapter
import com.example.wetherapp.databinding.FragmentAirBinding
import com.example.wetherapp.model.WeatherModel
import com.example.wetherapp.databinding.FragmentCurrentBinding
import com.example.wetherapp.databinding.ItemCurrentBinding
import com.example.wetherapp.model.AirQualityModel
import com.example.wetherapp.view.WeatherRadar


class AirQualityFragment : Fragment() {
    private lateinit var adapter: AirQualityAdapter
    private lateinit var binding: FragmentAirBinding
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAirBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        model.liveDataAir.observe(viewLifecycleOwner) {
            adapter.submitList(getCurrentList(it))
        }
    }

    private fun init() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = AirQualityAdapter()
        rcView.adapter = adapter
    }

    private fun getCurrentList(wItem: AirQualityModel): List<AirQualityModel> {
        val list = ArrayList<AirQualityModel>()
        list.add(wItem)
        return list
    }


    companion object {
        @JvmStatic
        fun newInstance() = AirQualityFragment()
    }
}