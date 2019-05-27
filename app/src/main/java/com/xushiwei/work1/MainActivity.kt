package com.xushiwei.work1

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.xushiwei.work1.first.FirstActivity
import com.xushiwei.work1.two.TwoActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_first.setOnClickListener {
            startActivity(Intent(this, FirstActivity::class.java))
        }
        tv_two.setOnClickListener {
            startActivity(Intent(this, TwoActivity::class.java))
        }
    }
}