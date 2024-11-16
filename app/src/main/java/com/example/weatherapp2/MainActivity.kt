package com.example.weatherapp2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var weatherService: WeatherService
    private lateinit var etCity: EditText
    private lateinit var btnGetWeather: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherService = retrofit.create(WeatherService::class.java)

        // Initialize UI elements
        etCity = findViewById(R.id.et_city)
        btnGetWeather = findViewById(R.id.btn_get_weather)

        // Set up button click listener
        btnGetWeather.setOnClickListener {
            val cityName = etCity.text.toString()
            if (cityName.isNotBlank()) {
                fetchWeather(cityName)
            }
        }
    }

    private fun fetchWeather(cityName: String) {
        val apiKey = "e2debe9974d3d81663e8478471183441"
        val units = "metric"  // Metric unit for Celsius

        weatherService.getCurrentWeather(cityName, apiKey, units)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weather = response.body()
                        if (weather != null) {
                            // Set TextViews using formatted strings from resources
                            findViewById<TextView>(R.id.tv_city).text =
                                getString(R.string.location_text, cityName)
                            findViewById<TextView>(R.id.tv_temperature).text =
                                getString(R.string.temperature_text, weather.main.temp)
                            findViewById<TextView>(R.id.tv_description).text =
                                getString(R.string.description_text, weather.weather[0].description)
                        } else {
                            Log.e("WeatherApp", "Weather response is null")
                        }
                    } else {
                        Log.e("WeatherApp", "Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("WeatherApp", "Failure: ${t.message}")
                }
            })
    }
}
