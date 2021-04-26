package com.yourapp.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.yourapp.weatherapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding
    lateinit var weatherModel: WeatherModel
    private lateinit var unit : Units

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        weatherModel = intent.extras?.get("extra_weatherModel") as WeatherModel
        val location = intent.extras?.get("extra_location") as String
        unit = intent.extras?.get("extra_unit") as Units


        binding.heading.text = location+ "'s " + binding.heading.text.toString()
        binding.maxTextView.text  = weatherModel.main?.temp_max.toString() + getUnit()
        binding.minTextView.text = weatherModel.main?.temp_min.toString() + getUnit()

        setSupportActionBar(binding.detailsToolbar)
        //binding.details
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    fun getUnit() : String {
        var ext : String = ""
        when(unit.ordinal){
            0 -> ext = "`C"
            1 -> ext = "`F"
            2 -> ext = "K"
        }
        return ext
    }
}