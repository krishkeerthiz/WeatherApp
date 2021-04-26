package com.yourapp.weatherapp

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.audiofx.BassBoost
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.style.MetricAffectingSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_DENIED
import androidx.lifecycle.ViewModelProvider
import com.yourapp.weatherapp.databinding.ActivityMainBinding
import java.io.Serializable


private const val TAG = "MainActivity"

private lateinit var binding : ActivityMainBinding

enum class Units {
    metric, imperial, standard,
}

private var unit = Units.metric

class MainActivity : AppCompatActivity() {


    private val viewModel : WeatherViewModel by lazy{
        ViewModelProvider(this).get(WeatherViewModel::class.java)
    }

    private val REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root


       // Toast.makeText(this, " unit index is ${unit.name}", Toast.LENGTH_SHORT).show()
        val units = resources.getStringArray(R.array.units)

        val continents = resources.getStringArray(R.array.continents)


        setContentView(view)

        checkPermission()

        binding.buttonGet.setOnClickListener(){
            val location = binding.editTextLocation.text.toString()
            viewModel.getWeatherReport(location, unit.name)
            hideKeyboard(it)
        }

        binding.DetailButton.setOnClickListener(){
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("extra_weatherModel", viewModel.weatherModel )
                putExtra("extra_location", binding.editTextLocation.text.toString())
                putExtra("extra_unit", unit)
            }

            startActivity(intent)
        }

        binding.voiceButton.setOnClickListener(){
            startVoiceRecognitionActivity()
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

        viewModel.temperature.observe(this, androidx.lifecycle.Observer {
            binding.tv.text = "Temperature :" + (it.toString()) + getUnit()
        })



        val unitsSpinnerAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item ,units)

        binding.unitsSpinner.adapter = unitsSpinnerAdapter

        binding.unitsSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Log.i(TAG, "on item selected")
                when(position){
                    0 -> {viewModel.getWeatherReport(binding.editTextLocation.text.toString(), "metric")
                            unit = Units.metric}
                    1 ->  {viewModel.getWeatherReport(binding.editTextLocation.text.toString(), "imperial")
                            unit = Units.imperial}
                    2 -> {viewModel.getWeatherReport(binding.editTextLocation.text.toString(), "standard")
                            unit = Units.standard}
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val continentsSpinnerAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, continents)

        binding.continentsSpinner.adapter = continentsSpinnerAdapter

        binding.continentsSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //"Not yet implemented"
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.getWeatherReport(continents[position], unit.name)
                binding.editTextLocation.setText(continents[position])
            }
        }

    }
    fun startVoiceRecognitionActivity()
    {

        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(listener())

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.packageName)
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice searching...")
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
       // intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

        speechRecognizer.startListening(intent)
        Log.i(TAG, "Speech Recognizer starts  listening")

        if(intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE)
            Log.i(TAG, "start activity for result called")
        }

        speechRecognizer.stopListening()
        speechRecognizer.destroy()
    }
    /**
     * Handle the results from the voice recognition activity.
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            val message  = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (message != null){
                binding.editTextLocation.setText(message[0])
                viewModel.getWeatherReport(message[0], unit.name)

            }

            Log.i(TAG, "on Activity result called")
        }
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_CODE)
                val intent = Intent(
                    ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
                finish()
            }
//            else
//                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
//        if(ContextCompat.checkSelfPermission(this , Manifest.permission.RECORD_AUDIO) == PERMISSION_DENIED){
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_CODE)
//        }
//        else
//            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
    }

     class listener : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {
            Log.d(TAG, "onReadyForSpeech")
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech")
        }

        override fun onRmsChanged(rmsdB: Float) {
            Log.d(TAG, "onRmsChanged")
        }

        override fun onBufferReceived(buffer: ByteArray) {
            Log.d(TAG, "onBufferReceived")
        }

        override fun onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech")
        }

        override fun onError(error: Int) {
            Log.d(TAG, "error $error")
        }

        override fun onResults(results: Bundle) {
            var str = String()
            Log.d(TAG, "onResults $results")
            val data: ArrayList<String>? = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (data != null) {
                for (i in 0 until data.size) {
                    Log.d(TAG, "result " + data[i])
                    str += data.get(i)
                }
            }

            binding.editTextLocation.setText(str)
        }

        override fun onPartialResults(partialResults: Bundle) {
            val data: ArrayList<String>? =
                partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val word = data?.get(data.size - 1) as String
            binding.editTextLocation.setText(word)

            Log.i("TEST", "partial_results: $word")
        }

        override fun onEvent(eventType: Int, params: Bundle) {
            Log.d(TAG, "onEvent $eventType")
        }
    }

}

//    fun parseJson(){
//
//
//        val service = retrofit.create(ApiServiceMoshi::class.java)
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = service.getWeather("London", "2e65127e909e178d0af311a81f39948c")
//
//            withContext(Dispatchers.Main){
//                if(response.isSuccessful) {
//                    val name = response.body()?.main?.temp.toString()
//                    message = name
//                    Log.d("Name" , name)
////                    if(items != null){
////
////                        for (i in 0 until items.count()) {
////                            // ID
////                            val id = items[i].employeeId ?: "N/A"
////                            Log.d("ID: ", id)
////
////                            // Employee Name
////                            val employeeName = items[i].employee?.name ?: "N/A"
////                            Log.d("Employee Name: ", employeeName)
////
////                            // Employee Salary in USD
////                            val employeeSalaryUSD = items[i].employee?.salary?.usd ?: 0
////                            Log.d("Employee Salary in USD:", employeeSalaryUSD.toString())
////
////                            // Employee Salary in EUR
////                            val employeeSalaryEUR = items[i].employee?.salary?.eur ?: 0
////                            Log.d("Employee Salary in EUR:", employeeSalaryEUR.toString())
////
////                            // Employee Age
////                            val employeeAge = items[i].employee?.age ?: "N/A"
////                            Log.d("Employee Age: ", employeeAge)
////                        }
////                    }
//
//                }
//                else{
//                            Log.e("Retrofit Error", response.body().toString())
//                        }
//
//                    }
//
//                }
//        }
//
//
//    }
