package com.example.firebasesetup

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class ExerciseActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        val breathingExercises = findViewById<Spinner>(R.id.BreathingExercises)
        val otherExercises = findViewById<Spinner>(R.id.OtherExercises)
        val recommendedFoods = findViewById<Spinner>(R.id.RecommendedFoods)
        val watchVideos = findViewById<Spinner>(R.id.WatchVideo)

        val FoodImage = findViewById<ImageView>(R.id.foodImageView)
        val webView = findViewById<WebView>(R.id.youtubeWebView)

        val breathingItems = listOf("Select Breathing Exercise", "Deep Breathing", "Calming Breath", "4-7-8 Breathing")
        val otherExercisesItems = listOf("Select Other Exercise", "Progressive Muscle Relaxation", "Mindful Breathing", "Yoga", "Pilates")
        val foodsItems = listOf("Select Food Recommendation", "Foods for Stress Relief and Relaxation", "Foods for Boosting Energy", "Foods for Mental Clarity and Focus", "Foods for Physical Fitness and Recovery", "Foods for Better Sleep")
        val videosItems = listOf("Select Video Type", "Fitness and Physical Exercise Videos", "Mental Health and Well-Being", "Sleep and Relaxation", "Learning and Educational Videos", "Funny Videos")

        val breathingAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, breathingItems)
        val otherExercisesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, otherExercisesItems)
        val foodsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, foodsItems)
        val videosAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, videosItems)

        breathingExercises.adapter = breathingAdapter
        otherExercises.adapter = otherExercisesAdapter
        recommendedFoods.adapter = foodsAdapter
        watchVideos.adapter = videosAdapter

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                if (url.contains("youtube.com") || url.contains("youtu.be")) {
                    return false
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                    return true
                }
            }
        }
        webView.settings.apply {
            javaScriptEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            domStorageEnabled = true
            setSupportMultipleWindows(false)
            builtInZoomControls = false
            displayZoomControls = false
        }

        breathingExercises.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedExercise = breathingItems[position]

                if (selectedExercise != "Select Breathing Exercise") {
                    Toast.makeText(applicationContext, "Selected: $selectedExercise", Toast.LENGTH_SHORT).show()

                    if (selectedExercise == "Deep Breathing") {
                        val intent = Intent(this@ExerciseActivity, BreathingActivity::class.java)
                        startActivity(intent)
                    } else if (selectedExercise == "Calming Breath") {
                        val intent = Intent(this@ExerciseActivity, CalmBreathActivity::class.java)
                        startActivity(intent)
                    }
                    else if(selectedExercise == "4-7-8 Breathing"){
                        val intent = Intent(this@ExerciseActivity, Breathing478Activity::class.java)
                        startActivity(intent)
                    }
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                breathingExercises.setSelection(0)
            }
        }

        otherExercises.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedExercise = otherExercisesItems[position]

                if (selectedExercise != "Select Other Exercise") {
                    webView.visibility = View.VISIBLE
                        if(selectedExercise == "Progressive Muscle Relaxation"){
                            val videoUrl = "https://youtu.be/ihO02wUzgkc?si=5cv-Y3l_2UxvhkZX"
                            webView.loadUrl(videoUrl)
                        }
                    else if(selectedExercise == "Mindful Breathing"){
                            val videoUrl = "https://youtu.be/wPoj5log_7M?si=T10tUozB9tyADUEh"
                            webView.loadUrl(videoUrl)
                        }
                    else if(selectedExercise == "Yoga"){
                            val videoUrl = "https://youtu.be/yqeirBfn2j4?si=xoKI5lHsw0hmAwnS"
                            webView.loadUrl(videoUrl)
                        }
                    else if(selectedExercise == "Pilates"){
                            val videoUrl = "https://www.youtube.com/watch?v=C2HX2pNbUCM&t=356s"
                            webView.loadUrl(videoUrl)
                        }

                } else {
                    webView.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                otherExercises.setSelection(0)
            }
        }

        recommendedFoods.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFood = foodsItems[position]

                if (selectedFood != "Select Food Recommendation") {
                    FoodImage.visibility = View.VISIBLE
                    when (selectedFood) {
                        "Foods for Stress Relief and Relaxation" -> FoodImage.setImageResource(R.drawable.stress)
                        "Foods for Boosting Energy" -> FoodImage.setImageResource(R.drawable.energy)
                        "Foods for Mental Clarity and Focus" -> FoodImage.setImageResource(R.drawable.mental)
                        "Foods for Physical Fitness and Recovery" -> FoodImage.setImageResource(R.drawable.physical)
                        "Foods for Better Sleep" -> FoodImage.setImageResource(R.drawable.sleep)
                        else -> FoodImage.visibility = View.GONE // Hide if no match is found
                    }
                } else {
                    FoodImage.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                recommendedFoods.setSelection(0)
            }
        }

        watchVideos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedVideo = videosItems[position]

                if (selectedVideo != "Select Video Type") {
                    webView.visibility = View.VISIBLE
                    if (selectedVideo == "Fitness and Physical Exercise Videos") {
                        val videoUrl = "https://youtu.be/I6t0quh8Ick?si=jO0dB5rKC708gfDd"
                        webView.loadUrl(videoUrl)
                    }
                    else if (selectedVideo == "Mental Health and Well-Being") {
                        val videoUrl = "https://youtu.be/2Vtz43pRPas?si=KPALcODDg6DaJGBT"
                        webView.loadUrl(videoUrl)
                    }
                    else if (selectedVideo == "Sleep and Relaxation") {
                        val videoUrl = "https://youtu.be/1ZYbU82GVz4?si=ak3iR_DmCFdZAA54"
                        webView.loadUrl(videoUrl)
                    }
                    else if(selectedVideo == "Learning and Educational Videos"){
                        val videoUrl = "https://youtu.be/HJLNtge8jv4?si=qeTTolRCjYJCOYtm"
                        webView.loadUrl(videoUrl)
                    }
                    else if(selectedVideo == "Funny Videos"){
                        val videoUrl = "https://youtu.be/INXUwpPoZfs?si=mxAlHj0Blc3hmk6P"
                        webView.loadUrl(videoUrl)
                    }
                } else {
                        webView.visibility = View.GONE                  }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                watchVideos.setSelection(0)
            }
        }
    }
}
