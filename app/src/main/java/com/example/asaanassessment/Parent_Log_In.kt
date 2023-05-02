package com.example.asaanassessment

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Parent_Log_In : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_log_in)




    }



    fun log_in(v: View) {
        val inputId = findViewById<TextView>(R.id.parent_id).text.toString()
        val inputPassword = findViewById<TextView>(R.id.teacher_password).text.toString()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Parent")

        val progressDialog = ProgressDialog.show(
            this@Parent_Log_In, // context
            "Authentication", // title
            "Loading. Please wait...", // message
            true // indeterminate
        )

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                runOnUiThread {


                    // Iterate over the children of the Parent node to check for a matching Parent ID and Password.
                    for (parentSnapshot in dataSnapshot.children) {
                        if (inputId.equals(parentSnapshot.key.toString()) && inputPassword.equals(parentSnapshot.child("Password").getValue(String::class.java).toString())) {
                            progressDialog.dismiss() // hide the dialog when the data is retrieved
                              val applicationBasedPref =getSharedPreferences("Parent", Context.MODE_PRIVATE)
                            val ed = applicationBasedPref.edit()

                            ed.putString("ParentName",parentSnapshot.child("ParentName").getValue(String::class.java).toString())
                            ed.putString("ParentId",parentSnapshot.key.toString())
                            ed.commit()

                            val intent = Intent(this@Parent_Log_In,Parent::class.java)
                            intent.putExtra("Name",parentSnapshot.child("ParentName").getValue(String::class.java).toString())
                            intent.putExtra("Id",parentSnapshot.key.toString())
                            startActivity(intent)
                            return@runOnUiThread
                        }
                    }

                    progressDialog.dismiss() // hide the dialog when the data is retrieved

                    Toast.makeText(this@Parent_Log_In,"Incorrect Parent ID or Password",Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss() // hide the dialog if there is an error
            }
        })


    }


}