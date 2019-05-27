package com.xushiwei.work1.first

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.xushiwei.work1.R
import kotlinx.android.synthetic.main.activity_first.*

class FirstActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
    }

    override fun onResume() {
        super.onResume()
        val decodeResource = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher)
//        decal_view.setBackgroundBitmap(decodeResource)
    }
}
