package com.example.asaanassessment

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



    }


fun parent(v: View)
{

    val parent = Intent(this, Parent_Log_In::class.java)
    startActivity(parent)
}

    fun teacher(v:View)
    {
        val teacher = Intent(this, Teacher_Log_In::class.java)
        startActivity(teacher)
    }
}