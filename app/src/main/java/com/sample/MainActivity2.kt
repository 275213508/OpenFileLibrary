package com.sample

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.faradaj.blurbehind.BlurBehind

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BlurBehind.getInstance()
//                .withAlpha(80)
//                .withFilterColor(Color.parseColor("#000000")) //or Color.RED
                .setBackground(this);
    }
}