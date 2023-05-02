package com.example.asaanassessment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val parent =getSharedPreferences("Parent", Context.MODE_PRIVATE)
        val teacher = getSharedPreferences("Teacher",Context.MODE_PRIVATE)

        if(parent.getString("ParentName",null)!=null) {
            val parentIntent = Intent(this, Parent::class.java)
            parentIntent.putExtra("Name",parent.getString("ParentName",null))


            parentIntent.putExtra("Id",parent.getString("ParentId",null))
            startActivity(parentIntent)

        }

        if(teacher.getString("TeacherName",null)!=null) {


                val teacherIntent = Intent(this, Teacher::class.java)

                teacherIntent.putExtra("Name",teacher.getString("TeacherName",null))
                teacherIntent.putExtra("Id",teacher.getString("TeacherId",null))

                startActivity(teacherIntent)

        }


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