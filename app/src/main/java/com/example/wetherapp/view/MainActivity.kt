package com.example.wetherapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import com.example.wetherapp.R
import com.example.wetherapp.controler.controler.MainFragment
import com.example.wetherapp.databinding.ActivityMainBinding
import com.example.wetherapp.databinding.FragmentMainBinding


class MainActivity : AppCompatActivity(), MainFragment.OnButtonClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var IBinding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IBinding = FragmentMainBinding.inflate(layoutInflater)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.weather_radar -> {
                    val intent = Intent(this@MainActivity, WeatherRadar::class.java)
                    startActivity(intent)
                }
                R.id.settings -> {
                    val intent = Intent(this@MainActivity, ThemeActivity::class.java)
                    startActivity(intent)
                }
            }
            binding.drawermain.closeDrawer(GravityCompat.START)
            true
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.placeholder, MainFragment.newInstance()).commit()
    }
    override fun onButtonClick() {
        binding.drawermain.openDrawer(GravityCompat.START)
    }

}

