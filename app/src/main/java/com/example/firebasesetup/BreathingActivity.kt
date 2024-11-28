package com.example.firebasesetup

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BreathingActivity : AppCompatActivity() {

    private var isPaused = false
    private lateinit var animator: ValueAnimator
    private lateinit var countdownTimer: CountDownTimer


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breathing)

        val tvTimer = findViewById<TextView>(R.id.tvTimer)
        val outerCircle = findViewById<View>(R.id.outerCircle)

        // Breathing Animation
        animator = ValueAnimator.ofFloat(1f, 1.2f).apply {
            duration = 3000 // Matches the timer duration
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                val scale = it.animatedValue as Float
                outerCircle.scaleX = scale
                outerCircle.scaleY = scale
            }
        }
        animator.start()

        // Countdown Timer
        startCountdownTimer(tvTimer)

        // Pause/Resume Animation and Timer
        val btnPause = findViewById<Button>(R.id.btnPause)
        btnPause.setOnClickListener {
            if (isPaused) {
                animator.start()
                startCountdownTimer(tvTimer)
                btnPause.text = "Pause"
            } else {
                animator.cancel()
                countdownTimer.cancel()
                btnPause.text = "Resume"
            }
            isPaused = !isPaused
        }
    }

    private fun startCountdownTimer(tvTimer: TextView) {
        countdownTimer = object : CountDownTimer(3000, 1000) { // 3 seconds with 1-second intervals
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                tvTimer.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                tvTimer.text = "3" // Reset timer text for the next cycle
                startCountdownTimer(tvTimer) // Restart the timer for the next cycle
            }
        }
        countdownTimer.start()
    }
}