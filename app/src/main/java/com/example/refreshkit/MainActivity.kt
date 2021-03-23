package com.example.refreshkit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.refreshkit.demo2.RefreshDemo2Activity
import com.example.refreshkit.demo1.RefreshDemo1Activity

class MainActivity : AppCompatActivity() , View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_i_refresh->{
                startActivity(Intent(this, RefreshDemo1Activity::class.java))
            }
            R.id.tv_i_loadmore->{
                startActivity(Intent(this, RefreshDemo2Activity::class.java))
            }

        }
    }
}