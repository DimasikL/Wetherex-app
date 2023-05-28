package com.example.wetherapp.controler.controler

import android.Manifest

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.wetherapp.model.MainViewModel
import com.example.wetherapp.model.WeatherModel
import com.example.wetherapp.databinding.FragmentMainBinding
import com.example.wetherapp.model.AirQualityModel
import com.example.wetherapp.view.fragments.AirQualityFragment
import com.example.wetherapp.view.fragments.CurrentFragment
import com.example.wetherapp.view.fragments.DaysFragment
import com.example.wetherapp.view.fragments.HoursFragment
import com.example.wetherapp.view.fragments.isPermissionGranted
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API_KEY = "1cbd68755a8946e9af163134232704"

class MainFragment : Fragment() {
    private lateinit var buttonClickListener: OnButtonClickListener
    private lateinit var fLocationClient: FusedLocationProviderClient
    private val fList = listOf(
        CurrentFragment.newInstance(),
        HoursFragment.newInstance(),
        DaysFragment.newInstance(),
        AirQualityFragment.newInstance()
    )
    private val tList = listOf("Current", "Hours", "Days", "Air")
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = binding.openMenu
        button.setOnClickListener {
            buttonClickListener.onButtonClick()
        }
        binding.progressBar.visibility = View.VISIBLE
        binding.txProgress.visibility = View.VISIBLE
        checkPermission()
        init()
        updateCurrentCard()
        getLocation()
    }


    override fun onResume() {
        super.onResume()
        val appSettings = AppSettings(requireContext())
        val savedBackground = appSettings.getBackground()
        if (savedBackground != null) {
            val backgroundResource =
                resources.getIdentifier(savedBackground, "drawable", requireContext().packageName)
            binding.background.setImageResource(backgroundResource)
        }
        checkLocation()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            buttonClickListener = context as OnButtonClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnButtonClickListener")
        }
    }

    private fun init() = with(binding) {
        fLocationClient = FusedLocationProviderClient(requireContext())
        val adapter = VpAdapter(activity as FragmentActivity, fList)
        vp.adapter = adapter
        TabLayoutMediator(tapLayout, vp) { tab, pos ->
            tab.text = tList[pos]
        }.attach()
        ibSync.setOnClickListener {
            checkLocation()
        }
        ibSearch.setOnClickListener {
            DialogManager.searchByName(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    if (name != null) {
                        requestWeatherData(name)
                    }
                }
            })
        }
    }

    private fun checkLocation() {
        if (isLocationEnabled()) {
            getLocation()
        } else {
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }

    }

    private fun isLocationEnabled(): Boolean {
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation() {
        if (!isLocationEnabled()) {
            Toast.makeText(requireContext(), "Location disabled", Toast.LENGTH_SHORT).show()
            return
        }
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener {
                requestWeatherData("${it.result.latitude}, ${it.result.longitude}")
            }
    }

    private fun updateCurrentCard() = with(binding) {
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            val maxminTemp = "${String.format("%.0f", it.maxTemp.toDouble())}°С/${
                String.format(
                    "%.0f",
                    it.minTemp.toDouble()
                )
            }°С"
            val temp = "${String.format("%.0f", it.currentTemp.toDouble())}°С"
            txData.text = it.time
            tvCity.text = it.city
            tvTemp.text = temp
            tvSkyCond.text = it.condition
            textView5.text = maxminTemp
            Picasso.get().load("https:" + it.ImageUrl).into(imWeather)

        }
    }

    private fun permissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestWeatherData(city: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&days=" +
                "3" +
                "&aqi=yes&alerts=no"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET, url, { result ->
                parseWeatherData(result)
            },
            { error ->
                Log.d("MyLog", "Error: $error")
                Toast.makeText(context, "You didn't spell the city name wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        )
        binding.progressBar.visibility = View.GONE
        binding.txProgress.visibility = View.GONE
        queue.add(request)
    }

    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseAirQuality(mainObject)
        parseCurrentData(mainObject, list[0])
    }

    private fun parseAirQuality(mainObject: JSONObject){
        val item = AirQualityModel(
            mainObject.getJSONObject("current").getJSONObject("air_quality").getString("co"),
            mainObject.getJSONObject("current").getJSONObject("air_quality").getString("o3"),
            mainObject.getJSONObject("current").getJSONObject("air_quality").getString("no2"),
            mainObject.getJSONObject("current").getJSONObject("air_quality").getString("so2"),
            mainObject.getJSONObject("current").getJSONObject("air_quality").getString("pm2_5"),
            mainObject.getJSONObject("current").getJSONObject("air_quality").getString("pm10"),
            mainObject.getJSONObject("current").getJSONObject("air_quality").getString("us-epa-index")
        )
        model.liveDataAir.value = item
    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name")
        for (i in 0 until daysArray.length()) {
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                name,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition").getString("text"),
                "",
                day.getJSONObject("day").getString("maxtemp_c"),
                day.getJSONObject("day").getString("mintemp_c"),
                day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                day.getJSONArray("hour").toString(),
                "", "", "", "", "", "", "", "", ""
            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel) {
        val day = mainObject.getJSONObject("forecast").getJSONArray("forecastday")[0] as JSONObject
        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c"),
            weatherItem.maxTemp,
            weatherItem.minTemp,
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            weatherItem.hours,
            mainObject.getJSONObject("location").getString("country"),
            mainObject.getJSONObject("current").getString("wind_kph"),
            mainObject.getJSONObject("current").getString("pressure_mb"),
            mainObject.getJSONObject("current").getString("cloud"),
            mainObject.getJSONObject("current").getString("wind_dir"),
            day.getJSONObject("astro").getString("sunrise"),
            day.getJSONObject("astro").getString("sunset"),
            day.getJSONObject("astro").getString("moon_phase"),
            mainObject.getJSONObject("current").getString("humidity")
        )
        model.liveDataCurrent.value = item
    }

    interface OnButtonClickListener {
        fun onButtonClick()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}