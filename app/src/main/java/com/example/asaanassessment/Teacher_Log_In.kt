package com.example.asaanassessment

import android.Manifest
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class Teacher_Log_In : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_log_in)
    }


    fun log_in(v: View) {

        val inputId = findViewById<TextView>(R.id.teacher_id).text.toString()
        val inputPassword = findViewById<TextView>(R.id.teacher_password).text.toString()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Teacher")

        val progressDialog = ProgressDialog.show(
            this@Teacher_Log_In, // context
            "Authentication", // title
            "Loading. Please wait...", // message
            true // indeterminate
        )

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                runOnUiThread {


                    // Iterate over the children of the Parent node to check for a matching Parent ID and Password.
                    for (teacherSnapshot in dataSnapshot.children) {
                        if (inputId.equals(teacherSnapshot.key.toString()) && inputPassword.equals(
                                teacherSnapshot.child("Password").getValue(String::class.java)
                                    .toString()
                            )
                        ) {
                            progressDialog.dismiss() // hide the dialog when the data is retrieved

                            val applicationBasedPref =
                                getSharedPreferences("Teacher", Context.MODE_PRIVATE)
                            val ed = applicationBasedPref.edit()

                            ed.putString(
                                "TeacherName",
                                teacherSnapshot.child("TeacherName").getValue(String::class.java)
                                    .toString()
                            )
                            ed.putString("TeacherId", teacherSnapshot.key.toString())
                            ed.commit()

                            val fcm_token = database.getReference("Teacher/$inputId")

                            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->


                                // Do something with the token, like storing it in your database
                                fcm_token.child("fcmToken").setValue(token)
                            }

                            val intent = Intent(this@Teacher_Log_In, Teacher::class.java)
                            intent.putExtra(
                                "Name",
                                applicationBasedPref.getString("TeacherName", null)
                            )

                            intent.putExtra("Id", applicationBasedPref.getString("TeacherId", null))
                            startActivity(intent)
                            return@runOnUiThread
                        }
                    }

                    progressDialog.dismiss() // hide the dialog when the data is retrieved

                    Toast.makeText(
                        this@Teacher_Log_In,
                        "Incorrect Parent ID or Password",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss() // hide the dialog if there is an error
            }
        })

    }

    }



    // This class handles the incoming Notifications
