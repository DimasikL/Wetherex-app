package com.example.wetherapp.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wetherapp.model.MainViewModel
import com.example.wetherapp.controler.controler.adapters.WeatherAdapter
import com.example.wetherapp.model.WeatherModel
import com.example.wetherapp.databinding.FragmentHoursBinding
import org.json.JSONArray
import org.json.JSONObject


class HoursFragment : Fragment() {
    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: WeatherAdapter
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            adapter.submitList(getHoursList(it))
        }
    }

    private fun initRcView() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherAdapter()
        rcView.adapter = adapter
        tvEmpty.visibility = View.INVISIBLE
    }

    private fun getHoursList(wItem: WeatherModel): List<WeatherModel> {
        val hours = JSONArray(wItem.hours)
        val list = ArrayList<WeatherModel>()
        for (i in 0 until hours.length()) {
            val item = WeatherModel(
                "",
                (hours[i] as JSONObject).getString("time"),
                (hours[i] as JSONObject).getJSONObject("condition").getString("text"),
                (hours[i] as JSONObject).getString("temp_c"),
                "",
                "",
                (hours[i] as JSONObject).getJSONObject("condition").getString("icon"),
                "", "", "", "", "", "", "", "", "", ""
            )
            list.add(item)
        }

        return list
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}
