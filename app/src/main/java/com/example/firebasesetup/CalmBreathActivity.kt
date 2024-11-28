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

class CalmBreathActivity : AppCompatActivity() {
    private lateinit var outerCircle: View
    private lateinit var tvTimer: TextView
    private lateinit var btnPause: Button

    private var handler = Handler(Looper.getMainLooper())
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calm_breath)

        outerCircle = findViewById(R.id.outerCircle)
        tvTimer = findViewById(R.id.tvTimer)
        btnPause = findViewById(R.id.btnPause)

        startCalmingBreathSequence()

        btnPause.setOnClickListener {
            isPaused = !isPaused
            if (isPaused) {
                handler.removeCallbacksAndMessages(null)
                tvTimer.text = "Paused"
                btnPause.text = "Resume"
            } else {
                startCalmingBreathSequence()
                btnPause.text = "Pause"
            }
        }
    }

    private fun startCalmingBreathSequence() {
        if (isPaused) return

        // Inhale Phase
        tvTimer.text = "Inhale"
        startBreathAnimation(4_000) // 4 seconds
        handler.postDelayed({
            if (isPaused) return@postDelayed
            startHoldPhase()
        }, 4_000)
    }

    private fun startHoldPhase() {
        tvTimer.text = "Hold"
        startBreathAnimation(7_000, scale = 1.0f) // Hold with no scaling
        handler.postDelayed({
            if (isPaused) return@postDelayed
            startExhalePhase()
        }, 7_000)
    }

    private fun startExhalePhase() {
        tvTimer.text = "Exhale"
        startBreathAnimation(8_000, scale = 0.8f) // Exhale with shrinking effect
        handler.postDelayed({
            if (isPaused) return@postDelayed
            startCalmingBreathSequence() // Repeat the cycle
        }, 8_000)
    }

    private fun startBreathAnimation(duration: Long, scale: Float = 1.2f) {
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