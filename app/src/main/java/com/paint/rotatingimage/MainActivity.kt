package com.paint.rotatingimage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val customView = findViewById<RotatingByTouchImageView>(R.id.my_custom_view)
        customView.minValue = -4f
        customView.maxValue = 22f
        customView.valueChanged = { it ->
            Log.d("TAG", "it = $it")
            Unit
        }
    }
}