package com.example.firebasesetup

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Breathing478Activity : AppCompatActivity() {
    private lateinit var outerCircle: View
    private lateinit var tvPhase: TextView
    private lateinit var btnPause: Button

    private val handler = Handler(Looper.getMainLooper())
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_breathing478)
        outerCircle = findViewById(R.id.outerCircle)
        tvPhase = findViewById(R.id.tvPhase)
        btnPause = findViewById(R.id.btnPause)

        start478BreathingSequence()

        btnPause.setOnClickListener {
            isPaused = !isPaused
            if (isPaused) {
                handler.removeCallbacksAndMessages(null)
                tvPhase.text = "Paused"
                btnPause.text = "Resume"
            } else {
                start478BreathingSequence()
                btnPause.text = "Pause"
            }
        }
    }

    private fun start478BreathingSequence() {
        if (isPaused) return

        // Inhale Phase (4 seconds)
        tvPhase.text = "Inhale"
        startBreathAnimation(4_000, 1.2f) // Outer circle grows
        handler.postDelayed({
            if (isPaused) return@postDelayed
            startHoldPhase()
        }, 4_000)
    }

    private fun startHoldPhase() {
        tvPhase.text = "Hold"
        startBreathAnimation(7_000, 1.0f) // Hold with no scaling
        handler.postDelayed({
            if (isPaused) return@postDelayed
            startExhalePhase()
        }, 7_000)
    }

    private fun startExhalePhase() {
        tvPhase.text = "Exhale"
        startBreathAnimation(8_000, 0.8f) // Outer circle shrinks
        handler.postDelayed({
            if (isPaused) return@postDelayed
            start478BreathingSequence() // Restart the cycle
        }, 8_000)
    }

    private fun startBreathAnimation(duration: Long, scale: Float) {
        val animator = ValueAnimator.ofFloat(1.0f, scale).apply {
            this.duration = duration
            repeatMode = ValueAnimator.REVERSE
            repeatCount = 1
            addUpdateListener {
                val animatedValue = it.animatedValue as Float
                outerCircle.scaleX = animatedValue
                outerCircle.scaleY = animatedValue
            }
        }
        animator.start()
    }
}

