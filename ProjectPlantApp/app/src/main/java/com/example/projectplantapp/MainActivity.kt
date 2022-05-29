package com.example.projectplantapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pdoc.setOnClickListener {
            val intent= Intent(this,scan::class.java)
            startActivity(intent)
        }
        prevent.setOnClickListener {
            val intent= Intent(this,CommomRemedies::class.java)
            startActivity(intent)
        }
        about.setOnClickListener {
            val intent= Intent(this,AboutUs::class.java)
            startActivity(intent)
        }
    }
}