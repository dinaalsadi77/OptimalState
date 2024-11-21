package com.example.firebasesetup

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class ExerciseActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_exercise)

        val breathingExercises = findViewById<Spinner>(R.id.BreathingExercises)
        val breathingItems = listOf("Select Breathing Exercise", "Deep Breathing", "Calming Breath", "4-7-8 Breathing")
        val breathingAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, breathingItems)

        val otherExercises = findViewById<Spinner>(R.id.OtherExercises)
        val otherExercisesItems = listOf("Select Other Exercise", "Progressive Muscle Relaxation", "Mindful Breathing", "Yoga", "Pilates")
        val otherExercisesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, otherExercisesItems)

        val recommendedFoods = findViewById<Spinner>(R.id.RecommendedFoods)
        val foodsItems = listOf("Select Food Recommendation", "Foods for Stress Relief and Relaxation", "Foods for Boosting Energy", "Foods for Mental Clarity and Focus", "Foods for Physical Fitness and Recovery", "Foods for Better Sleep")
        val foodsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, foodsItems)

        val watchVideos = findViewById<Spinner>(R.id.WatchVideo)
        val videosItems = listOf("Select Video Type", "Fitness and Physical Exercise Videos", "Mental Health and Well-Being", "Sleep and Relaxation", "Learning and Educational Videos", "Funny Videos")
        val videosAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, videosItems)

        breathingExercises.adapter = breathingAdapter
        otherExercises.adapter = otherExercisesAdapter
        recommendedFoods.adapter = foodsAdapter
        watchVideos.adapter = videosAdapter

        breathingExercises.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedExercise = breathingItems[position]

                if (selectedExercise != "Select Breathing Exercise") {
                    Toast.makeText(applicationContext, "Selected: $selectedExercise", Toast.LENGTH_SHORT).show()



                } else {
                    Toast.makeText(applicationContext, "Please select a valid breathing exercise", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                breathingExercises.setSelection(0)
            }
        }

        otherExercises.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedExercise = otherExercisesItems[position]

                if (selectedExercise != "Select Other Exercise") {
                    Toast.makeText(applicationContext, "Selected: $selectedExercise", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Please select a valid other exercise", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(applicationContext, "Selected: $selectedFood", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Please select a valid food recommendation", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(applicationContext, "Selected: $selectedVideo", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Please select a valid video type", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                watchVideos.setSelection(0)
            }
        }
    }
}
