package com.example.asaanassessment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*


class SendReminderTeacherFragment(val teacher:String) : Fragment() {
    private lateinit var mContext: Context
    lateinit var subjectIds: MutableList<String>
    lateinit var parentsIds: MutableList<String>
    lateinit var subjectNames: MutableList<String>
    var SubjectIndexSelected: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_send_reminder_teacher, container, false)


        val database = FirebaseDatabase.getInstance()
        val subjectsRef = database.getReference("Subject")



        val autoCompleteTextViewSubject: AutoCompleteTextView =
            view.findViewById(R.id.teacher_subject_selection)


        subjectsRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {


                subjectNames = mutableListOf<String>()
                subjectIds = mutableListOf<String>()
                for (subjectSnapshot in dataSnapshot.children) {
                    val subjectName =
                        subjectSnapshot.child("CourseName").getValue(String::class.java)

                    val TeacherID = subjectSnapshot.child("TeacherId").getValue(String::class.java)
                    if (subjectName != null && TeacherID.equals(teacher)) {

                        subjectNames.add(subjectName)
                        subjectIds.add(subjectSnapshot.key.toString())
                    }
                }

                val items = mutableListOf<String>()

                for (index in subjectNames.indices) {
                    items.add(subjectNames[index] + "  -  " + subjectIds[index])
                }
                val adapter =
                    ArrayAdapter(mContext, android.R.layout.simple_dropdown_item_1line, items)
                autoCompleteTextViewSubject.setAdapter(adapter)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(mContext, databaseError.toException().toString(), Toast.LENGTH_SHORT)
            }


        }


        )

        autoCompleteTextViewSubject.setOnItemClickListener { parent, view, position, id ->

            SubjectIndexSelected = position

        }

        val sendReminderBtn = view.findViewById<Button>(R.id.sendreminder)

        sendReminderBtn.setOnClickListener {


            val title = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.notificationtitle).editText?.text

            val description =  view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.notificationdescription).editText?.text

            if(SubjectIndexSelected!=-1)
            {

                if(title.toString().equals(""))
                {
                    Toast.makeText(mContext,"Enter a title! ",Toast.LENGTH_SHORT).show()
                }
                else
                {
                    if(description.toString().equals(""))
                    {
                        Toast.makeText(mContext,"Enter a description! ",Toast.LENGTH_SHORT).show()
                    }
                    else
                    {

                        val progressDialog = ProgressDialog.show(
                            mContext, // context
                            "Sending Reminder", // title
                            "Loading. Please wait...", // message
                            true // indeterminate
                        )
                        parentsIds=mutableListOf<String>()

                        val students = database.getReference("Student")

                        students.addListenerForSingleValueEvent(object :ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {


                                    for (student in snapshot.children) {
                                        for ((subIndex, subjects) in student.child("Subjects").children.withIndex()) {

                                            val databaseSubject=subjects.value.toString()


                                            if (
                                                    databaseSubject.equals(subjectIds[SubjectIndexSelected])
                                            ) {
                                                parentsIds.add(
                                                    student.child("ParentId")
                                                        .getValue(String::class.java).toString()
                                                )
                                                break
                                            }
                                        }
                                    }






                                    for (parentId in parentsIds) {


                                        val notificationRef = FirebaseDatabase.getInstance()
                                            .getReference("Parent/$parentId/Notification")
                                        // Generate a unique key for the homework node
                                        val notificationKey = notificationRef.push().key!!

                                        // Create a new homework object with the given fields
                                        val notification = HashMap<String, Any>()

                                        notification["sender"] = "Teacher"
                                        notification["time"] = getCurrentTime()
                                        notification["date"] = getCurrentDate()
                                        notification["title"] = title.toString()
                                        notification["description"] = description.toString()
                                        notification["isSeen"] = false
                                        notification["isReminder"] = true


                                        // Put the homework object at the generated key
                                        notificationRef.child(notificationKey)
                                            .setValue(notification)



                                    }

                                title?.clear()
                                description?.clear()
                                    progressDialog.dismiss()



                                }


                            override fun onCancelled(error: DatabaseError) {

                            }


                        })



                    }
                }
            }



        }
        return view

    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    private fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        val currentTime = Date()
        return timeFormat.format(currentTime)
    }
}