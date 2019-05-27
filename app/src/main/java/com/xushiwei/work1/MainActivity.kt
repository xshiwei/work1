package com.xushiwei.work1

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        val decodeResource = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher)
        decal_view.setBackgroundBitmap(decodeResource)
    }
}
