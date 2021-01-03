package me.juangoncalves.mentra.features.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.juangoncalves.mentra.features.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        startActivity(Intent(this, DashboardActivity::class.java))
        startActivity(Intent(this, OnboardingActivity::class.java))
        finish()
    }

}