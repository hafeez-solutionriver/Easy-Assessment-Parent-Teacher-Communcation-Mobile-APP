package com.example.asaanassessment


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.*
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class ProvideFeedbackTeacherFragment(val teacher:String) : Fragment() {


    var StudentIndexSelected: Int = -1
    var SubjectIndexSelected: Int = -1
    var startRating: Float = 0.0F
    lateinit var subjectIds: MutableList<String>
    lateinit var subjectNames: MutableList<String>
    lateinit var parentFcms: MutableList<String>

    lateinit var studentIds: MutableList<String>
    lateinit var studentNames: MutableList<String>


    lateinit var studentParentIds: MutableList<String>
    private lateinit var mContext: Context
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
        val view = inflater.inflate(R.layout.fragment_provide_feedback_teacher, container, false)


        val autoCompleteTextViewSubject: AutoCompleteTextView =
            view.findViewById(R.id.teacher_subject_selection_feedback)

        val database = FirebaseDatabase.getInstance()
        val subjectsRef = database.getReference("Subject")



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


            val studentRef = database.getReference("Student")



            studentRef.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {


                    studentNames = mutableListOf<String>()
                    studentIds = mutableListOf<String>()
                    studentParentIds = mutableListOf<String>()
                    parentFcms = mutableListOf<String>()
                    for (studentSnapshot in dataSnapshot.children) {
                        val studentName =
                            studentSnapshot.child("FirstName").getValue(String::class.java)

                        if (studentName != null) {

                            val parentIdDatabase = studentSnapshot.child("ParentId").getValue(String::class.java)
                                .toString()
                            studentNames.add(studentName)
                            studentIds.add(studentSnapshot.key.toString())
                            studentParentIds.add(parentIdDatabase)

                            val parentReference = FirebaseDatabase.getInstance().getReference("Parent/$parentIdDatabase")

                            parentReference.addListenerForSingleValueEvent(object :ValueEventListener
                            {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    parentFcms.add(snapshot.child("fcmToken").getValue(String::class.java).toString())

                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            }
                            )



                        }
                    }

                    val items = mutableListOf<String>()

                    for (index in studentNames.indices) {
                        items.add(studentNames[index] + "  -  " + studentIds[index])
                    }
                    val autoCompleteTextViewStudent: AutoCompleteTextView =
                        requireView().findViewById(R.id.teacher_student_selection_feedback)

                    autoCompleteTextViewStudent.onItemSelectedListener

                    autoCompleteTextViewStudent.setOnItemClickListener { parent, view, position, id ->

                        StudentIndexSelected = position
                    }
// Create an ArrayAdapter with the items and set it to the AutoCompleteTextView
                    val adapterStudent =
                        ArrayAdapter(mContext, android.R.layout.simple_dropdown_item_1line, items)
                    autoCompleteTextViewStudent.setAdapter(adapterStudent)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        mContext,
                        databaseError.toException().toString(),
                        Toast.LENGTH_SHORT
                    )
                }


            }


            )


        }


// Add an item click listener to the AutoCompleteTextView

        val ratingBar =
            view.findViewById<me.zhanghai.android.materialratingbar.MaterialRatingBar>(R.id.rating)

        val send = view.findViewById<Button>(R.id.submit_button)




        send.setOnClickListener {

            if (StudentIndexSelected != -1 && SubjectIndexSelected != -1) {

                val review =
                    view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.teacherReviewField).editText?.text.toString()
                val subjectId = subjectIds[SubjectIndexSelected]
                val parentId = studentParentIds[StudentIndexSelected]
                val studentId = studentIds[StudentIndexSelected]
                val assignmentName =
                    view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.teacherAssignmentNameField).editText?.text.toString()

                if (review.isEmpty()) {
                    Toast.makeText(mContext, "Please write a review!", Toast.LENGTH_SHORT).show()
                } else if (ratingBar.rating <= 0.0) {
                    Toast.makeText(mContext, "Please select rating!", Toast.LENGTH_SHORT).show()
                } else {

                    val progressDialog = ProgressDialog.show(
                        mContext, // context
                        "Sending", // title
                        "Loading. Please wait...", // message
                        true // indeterminate
                    )
                    // Query the Homework path for existing reviews with the same subject, student, and assignment
                    val homeworkRef = FirebaseDatabase.getInstance().getReference("Homework")
                    homeworkRef.orderByChild("subjectId").equalTo(subjectId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                var reviewExists = false
                                for (snapshot in dataSnapshot.children) {
                                    val existingHomework = snapshot.getValue(Homework::class.java)
                                    if (existingHomework != null && existingHomework.studentId == studentId && existingHomework.assignmentName == assignmentName && existingHomework.subjectId == subjectId) {
                                        reviewExists = true
                                        break
                                    }
                                }

                                if (reviewExists) {
                                    Toast.makeText(
                                        mContext,
                                        "You have already reviewed this assignment for this student!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    progressDialog.dismiss()
                                } else {


                                    // Generate a unique key for the homework node
                                    val homeworkKey = homeworkRef.push().key!!

                                    // Create a new homework object with the given fields
                                    val homework = HashMap<String, Any>()
                                    homework["subjectId"] = subjectId

                                    homework["teacherId"] = teacher
                                    homework["subjectName"] = subjectNames[SubjectIndexSelected]
                                    homework["parentId"] = parentId
                                    homework["studentId"] = studentId
                                    homework["studentName"] = studentNames[StudentIndexSelected]
                                    homework["assignmentName"] = assignmentName
                                    homework["review"] = review
                                    homework["rating"] = ratingBar.rating
                                    homework["parentReply"] = ""

                                    // Put the homework object at the generated key
                                    homeworkRef.child(homeworkKey).setValue(homework)


                                    val notificationRef = FirebaseDatabase.getInstance()
                                        .getReference("Parent/$parentId/Notification")
                                    // Generate a unique key for the homework node
                                    val notificationKey = notificationRef.push().key!!

                                    // Create a new homework object with the given fields
                                    val notification = java.util.HashMap<String, Any>()

                                    notification["sender"] = "Teacher"
                                    notification["time"] = getCurrentTime()
                                    notification["date"] = getCurrentDate()
                                    notification["title"] = "Teacher feedback"
                                    notification["description"] =
                                        "${studentNames[StudentIndexSelected]} teacher has provided feedback on homework module $assignmentName in ${subjectNames[SubjectIndexSelected]} subject."
                                    notification["isSeen"] = false
                                    notification["isReminder"] = false
                                    // Put the homework object at the generated key
                                    notificationRef.child(notificationKey)
                                        .setValue(notification)







                                    runBlocking {
                                        launch {



                                            Toast.makeText(mContext,"launch:${parentFcms[StudentIndexSelected]}",Toast.LENGTH_SHORT).show()
                                            sendPushNotification(
                                                parentFcms[StudentIndexSelected],
                                                "Teacher feedback",
                                                "${studentNames[StudentIndexSelected]} teacher has provided feedback on homework module $assignmentName in ${subjectNames[SubjectIndexSelected]} subject.",
                                                "Parent"
                                            )

                                        }}
                                    progressDialog.dismiss()

                                    view.findViewById<com.google.android.material.textfield.TextInputLayout>(
                                        R.id.teacherReviewField
                                    ).editText?.text?.clear()


                                    Toast.makeText(
                                        mContext,
                                        "Successfully sent.",
                                        Toast.LENGTH_SHORT
                                    ).show()


                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Toast.makeText(
                                    mContext,
                                    "Error: " + databaseError.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressDialog.dismiss()
                            }
                        })
                }
            } else
                Toast.makeText(mContext, "Please select a student!", Toast.LENGTH_SHORT).show()


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

    suspend fun sendPushNotification(token: String, title: String, message: String,receiver:String) {

        if(!token.equals(""))
        {
            Toast.makeText(mContext,"Sending notifcation to parent...",Toast.LENGTH_SHORT).show()
            Toast.makeText(mContext,token,Toast.LENGTH_SHORT).show()
            val fcmUrl = "https://fcm.googleapis.com/fcm/send"
            val serverKey = "key=AAAA3YWX8r4:APA91bFBYj4nf1WBsCcz_RhxJwPGnenGaDmw3sZ6EwWsIuwv8q3QaXFn79fQgdadlPwqCSZ3te9dhSK9JoE-Nutbz3AT1gyQNEfgZdGl_1X-ObwfJSGUV6P5PLOXxqQB7iN4ZViQNinr"

            val payload = mapOf(
                "notification" to mapOf(
                    "title" to title,
                    "body" to message,
                    "receiver" to receiver

                ),
                "to" to token
            )


            val options = mapOf(
                "method" to "POST",
                "headers" to mapOf(
                    "Content-Type" to "application/json",
                    "Authorization" to "key=$serverKey"
                ),
                "body" to payload
            )

            try {
                val response = withContext(Dispatchers.IO) {
                    val url = URL(fcmUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    options.forEach { (key, value) ->
                        connection.setRequestProperty(key, value.toString())
                    }
                    connection.inputStream.bufferedReader().use { it.readText() }
                }
                println("Push notification sent: $response")
            } catch (error: Exception) {
                println("Error sending push notification: $error")
            }
        }

    }
}
class Homework (
    var subjectId: String = "",
    var subjectName:String="",
    var parentId: String = "",
    var studentId: String = "",
    var assignmentName: String = "",
    var review: String = "",
    var rating: Float = 0f,
    var parentReply: String = ""
)

