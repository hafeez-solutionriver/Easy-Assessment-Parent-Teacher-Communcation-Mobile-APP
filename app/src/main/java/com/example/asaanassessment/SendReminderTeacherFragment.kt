package com.example.asaanassessment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class SendReminderTeacherFragment(val teacher:String) : Fragment() {
    private lateinit var mContext: Context
    lateinit var subjectIds: MutableList<String>
    lateinit var parentsIds: MutableList<String>
    lateinit var subjectNames: MutableList<String>
    lateinit var parentFcms: MutableList<String>


    lateinit var studentNames: MutableList<String>
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

            parentsIds = mutableListOf<String>()
            parentFcms = mutableListOf<String>()
            studentNames = mutableListOf<String>()

            val students = database.getReference("Student")

            students.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {


                    for (student in snapshot.children) {

                        for ((subIndex, subjects) in student.child("Subjects").children.withIndex()) {

                            val databaseSubject = subjects.value.toString()


                            if (
                                databaseSubject.equals(subjectIds[SubjectIndexSelected])
                            ) {
                                val parentId = student.child("ParentId")
                                    .getValue(String::class.java).toString()
                                parentsIds.add(

                                    parentId


                                )
                                studentNames.add(
                                    student.child("FirstName")
                                        .getValue(String::class.java).toString()
                                )


                                val reference =
                                    FirebaseDatabase.getInstance().reference.child("Parent")
                                        .child(parentId).child("fcmToken")

                                reference.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val fcmToken = dataSnapshot.value as? String
                                        if (fcmToken != null) {


                                            parentFcms.add(fcmToken)

                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        // Handle any error that occurred during retrieval
                                    }
                                })
                                break
                            }
                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

            val sendReminderBtn = view.findViewById<Button>(R.id.sendreminder)

            sendReminderBtn.setOnClickListener {


                val title =
                    view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.notificationtitle).editText?.text

                val description =
                    view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.notificationdescription).editText?.text

                if (SubjectIndexSelected != -1) {

                    if (title.toString().equals("")) {
                        Toast.makeText(mContext, "Enter a title! ", Toast.LENGTH_SHORT).show()
                    } else {
                        if (description.toString().equals("")) {
                            Toast.makeText(mContext, "Enter a description! ", Toast.LENGTH_SHORT)
                                .show()
                        } else {




                            callNotificaiton(title, description)
                            view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.notificationtitle).editText?.text?.clear()
                            view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.notificationdescription).editText?.text?.clear()
                            Toast.makeText(mContext, "Successfully sent ", Toast.LENGTH_SHORT)
                                .show()


                        }


                    }
                }
            }


        return view

    }



    private fun callNotificaiton(
        title: Editable?,
        description: Editable?
    ) {

        for (index in parentFcms.indices) {



            val notificationRef = FirebaseDatabase.getInstance()
                .getReference("Parent/${parentsIds[index]}/Notification")
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

            sendNotificaitonUsinfokhttp(
                parentFcms[index],
                "${studentNames[index]} teacher has sent an announcement, click to check it."
            )


        }
    }

    fun sendNotificaitonUsinfokhttp(token:String,body:String) {



        GlobalScope.launch(Dispatchers.IO) {
            val url = "https://fcm.googleapis.com/fcm/send"
            val serverKey = "AAAA3YWX8r4:APA91bFBYj4nf1WBsCcz_RhxJwPGnenGaDmw3sZ6EwWsIuwv8q3QaXFn79fQgdadlPwqCSZ3te9dhSK9JoE-Nutbz3AT1gyQNEfgZdGl_1X-ObwfJSGUV6P5PLOXxqQB7iN4ZViQNinr"

            val json = JSONObject()
            val data = JSONObject()
            data.put("title", "Teacher Announcement")
            data.put("body", body)
            data.put("receiver", "Parent")
            json.put("data", data)
            json.put("to", token)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toString().toRequestBody(mediaType)



            val request = okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Content-Type", "application/json")
                .header("Authorization", "key=$serverKey")
                .build()

            val client = OkHttpClient()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {

                    return@launch
                }

                // Notification sent successfully

            }

        }

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