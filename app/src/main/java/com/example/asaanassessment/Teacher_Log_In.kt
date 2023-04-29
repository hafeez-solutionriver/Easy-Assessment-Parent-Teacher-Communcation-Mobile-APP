package com.example.asaanassessment

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Teacher_Log_In : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_log_in)
    }


    fun log_in(v: View) {


//        val inputId = findViewById<TextView>(R.id.teacher_id).text.toString()
//        val inputPassword = findViewById<TextView>(R.id.teacher_password).text.toString()
//        val database = FirebaseDatabase.getInstance()
//        val myRef = database.getReference("Teacher")
//
//        val progressDialog = ProgressDialog.show(
//            this@Teacher_Log_In, // context
//            "Authentication", // title
//            "Loading. Please wait...", // message
//            true // indeterminate
//        )
//
//        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                runOnUiThread {
//
//
//                    // Iterate over the children of the Parent node to check for a matching Parent ID and Password.
//                    for (teacherSnapshot in dataSnapshot.children) {
//                        if (inputId.equals(teacherSnapshot.key.toString()) && inputPassword.equals(teacherSnapshot.child("Password").getValue(String::class.java).toString())) {
//                            progressDialog.dismiss() // hide the dialog when the data is retrieved
//                            val intent = Intent(this@Teacher_Log_In,Teacher::class.java)
//                            intent.putExtra("Name",teacherSnapshot.child("TeacherName").getValue(String::class.java).toString())
//                            startActivity(intent)
//                            return@runOnUiThread
//                        }
//                    }
//
//                    progressDialog.dismiss() // hide the dialog when the data is retrieved
//
//                    Toast.makeText(this@Teacher_Log_In,"Incorrect Parent ID or Password", Toast.LENGTH_LONG).show()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                progressDialog.dismiss() // hide the dialog if there is an error
//            }
//        })
        val intent = Intent(this,Teacher::class.java)
        intent.putExtra("Name","Hafeez")
        startActivity(intent)
    }
}