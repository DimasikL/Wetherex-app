package com.example.wetherapp.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.wetherapp.R
import com.example.wetherapp.controler.controler.AppSettings
import com.example.wetherapp.databinding.ActivityThemeBinding

class ThemeActivity : AppCompatActivity() {
    private lateinit var spinner: Spinner
    private lateinit var binding: ActivityThemeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        spinner = binding.spinner
        val colors = resources.getStringArray(R.array.background_colors)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val appSettings = AppSettings(this)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (parent.getItemAtPosition(position).toString()) {
                    "Standart theme" -> {
                        binding.constL.setBackgroundResource(R.drawable.background3)
                        appSettings.saveBackground(R.drawable.background3.toString())
                    }

                    "Sunset theme" -> {
                        binding.constL.setBackgroundResource(R.drawable.background2)
                        appSettings.saveBackground(R.drawable.background2.toString())
                    }

                    "Sunrise theme" -> {
                        binding.constL.setBackgroundResource(R.drawable.background)
                        appSettings.saveBackground(R.drawable.background.toString())
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }
}