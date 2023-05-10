package com.example.asaanassessment


import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class ShowFeedbackParentFragment(val parent:String) : Fragment(){


    lateinit var subjectNames: MutableList<String>
    lateinit var assignmentNames: MutableList<String>
    lateinit var studentIds: MutableList<String>
    lateinit var studentNames: MutableList<String>
    lateinit var reviews: MutableList<String>
    lateinit var homeworkIds: MutableList<String>
    lateinit var parentsReply: MutableList<String>
    lateinit var teachersIds: MutableList<String>
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
        val view = inflater.inflate(R.layout.fragment_show_feedback_parent, container, false)

        val database = FirebaseDatabase.getInstance()
        val listView = view.findViewById<ListView>(R.id.list_view_parent_showFeedback)
        val homeworkRef = database.getReference("Homework")
        val progressDialog = ProgressDialog(mContext)
        progressDialog.setMessage("Fetching data...")
        progressDialog.setCancelable(false)
        progressDialog.show()



        homeworkRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {


                progressDialog.dismiss()

                subjectNames = mutableListOf<String>()
                teachersIds = mutableListOf<String>()
                assignmentNames = mutableListOf<String>()
                studentIds = mutableListOf<String>()
                studentNames = mutableListOf<String>()
                reviews = mutableListOf<String>()
                parentsReply = mutableListOf<String>()
                homeworkIds = mutableListOf<String>()


                for (homeworkSnapshot in dataSnapshot.children) {

                    val databaseParentId =
                        homeworkSnapshot.child("parentId").getValue(String::class.java)

                    if (databaseParentId != null && databaseParentId.equals(
                           parent
                        )
                    ) {

                        teachersIds.add(homeworkSnapshot.child("parentId").getValue(String::class.java)
                            .toString())
                        homeworkIds.add(homeworkSnapshot.key.toString())
                        studentNames.add(
                            homeworkSnapshot.child("studentName").getValue(String::class.java)
                                .toString()
                        )
                        studentIds.add(
                            homeworkSnapshot.child("studentId").getValue(String::class.java)
                                .toString()
                        )
                        assignmentNames.add(
                            homeworkSnapshot.child("assignmentName").getValue(String::class.java)
                                .toString()
                        )
                        reviews.add(
                            homeworkSnapshot.child("review").getValue(String::class.java).toString()
                        )

                        subjectNames.add(
                            homeworkSnapshot.child("subjectName").getValue(String::class.java)
                                .toString()
                        )

                        parentsReply.add(
                            homeworkSnapshot.child("parentReply").getValue(String::class.java)
                                .toString()
                        )


                    }
                }

                val items = mutableListOf<CustomListItem>()

                for (index in studentNames.indices) {
                    items.add(
                        CustomListItem(

                            homeworkIds[index],
                            subjectNames[index],
                            assignmentNames[index],
                            studentNames[index] + "-" + studentIds[index],
                            reviews[index],
                            parentsReply[index],
                            teachersIds[index]


                        )
                    )
                }

                val adapter = CustomListAdapter(mContext, items)
                listView.adapter = adapter

            }

            override fun onCancelled(databaseError: DatabaseError) {

                progressDialog.dismiss()
                Toast.makeText(
                    mContext,
                    databaseError.toException().toString(),
                    Toast.LENGTH_SHORT
                )
            }


        })

        return view
    }




}

class CustomListAdapter(private val context: Context, private val data: List<CustomListItem>) :
    ArrayAdapter<CustomListItem>(context, R.layout.custom_list_view_parent, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.custom_list_view_parent, parent, false)

        val subjectTextView = itemView.findViewById<TextView>(R.id.subject_name)
        val homeworkTextView = itemView.findViewById<TextView>(R.id.homework_name)
        val studentTextView = itemView.findViewById<TextView>(R.id.student_name_with_id)
        val reviewTextView = itemView.findViewById<TextView>(R.id.review)
        val currentItem = data[position]

        val currentHomeworkID = currentItem.homeWorkId
        val currentTeacherId = currentItem.teacherId
        val currentStudentNameAndId=currentItem.studentNameWithId
        val currentHomeworkName=currentItem.homeworkName
        val currentSubjectName=currentItem.subjectName
        val btn = itemView.findViewById<Button>(R.id.translate_button)

        val giveReply = itemView.findViewById<Button>(R.id.reply_button)

        giveReply.setOnClickListener {
            if (giveReply.text.equals("Give Reply")) {
                val dialog = Dialog(context)
                dialog.setContentView(R.layout.give_reply_dialog)

                val sendButton = dialog.findViewById<Button>(R.id.send_button)
                val textField = dialog.findViewById<EditText>(R.id.text_field)

                sendButton.setOnClickListener {
                    val progress = ProgressDialog(context)
                    progress.setMessage("Sending reply...")
                    progress.setCancelable(false)
                    progress.show()

                    val reply = textField.text.toString()
                    // now store this reply firebase

                    // Get a reference to your database
                    val database = FirebaseDatabase.getInstance()

                    // Get a reference to the "Homework" node


                    // Get a reference to the "parentReply" node under the specific homework id
                    val parentReplyRef = database.getReference("Homework/$currentHomeworkID/parentReply")

                    // Set the value for parentReply
                    parentReplyRef.setValue(reply).addOnCompleteListener {

                        if (it.isSuccessful) {





                            val notificationRef = FirebaseDatabase.getInstance()
                                .getReference("Teacher/$currentTeacherId/Notification")
                            // Generate a unique key for the homework node
                            val notificationKey = notificationRef.push().key!!

                            // Create a new homework object with the given fields
                            val notification = HashMap<String, Any>()

                            notification["sender"] = "Parent"
                            notification["time"] = getCurrentTime()
                            notification["date"] = getCurrentDate()
                            notification["title"] = "Parent reply"
                            notification["description"] =
                                "$currentStudentNameAndId parent has given reply on your feedback of module $currentHomeworkName in $currentSubjectName subject."
                            notification["isSeen"] = false
                            notification["isReminder"] = false


                            // Put the homework object at the generated key
                            notificationRef.child(notificationKey)
                                .setValue(notification)


                            progress.dismiss()
                        }
                    }
                    dialog.dismiss() // Dismiss the dialog box after the operation is completed
                }
                dialog.show()
            } else {
                val alertDialog = AlertDialog.Builder(context).create()
                alertDialog.setTitle("Your Reply")


                alertDialog.setMessage(currentItem.parentReply)
                alertDialog.show()
            }
        }



        btn.setOnClickListener {


            val review = currentItem.review

            // Create an AlertDialog
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Select your language")
            builder.setIcon(R.drawable.baseline_language_24)
            builder.setItems(arrayOf("Sindhi", "Urdu")) { dialog, which ->
                // Get the selected language
                val language = when (which) {
                    0 -> "sd"
                    1 -> "ur"
                    else -> ""
                }

                // Create a progress dialog
                val progressDialog = ProgressDialog(context)
                progressDialog.setMessage("Translating...")
                progressDialog.show()

                // Run the network call on a separate thread
                Thread {
                    try {
                        // Create a Translate client.
                        val options = TranslateOptions.newBuilder()
                            .setApiKey("AIzaSyAjzqPvfa_73xPMjJTHnTbFAr8IQjn9HU8")
                            .build()
                        val translate = options.service

                        // Translate the text
                        val translation = translate.translate(
                            review,
                            Translate.TranslateOption.sourceLanguage("en"),
                            Translate.TranslateOption.targetLanguage(language)
                        )
                        val translatedText = translation.translatedText

                        // Update the UI on the main thread
                        it.post {
                            // Dismiss the progress dialog
                            progressDialog.dismiss()

                            // Display the translated text
                            val alertDialog = AlertDialog.Builder(context).create()
                            alertDialog.setTitle("Review")
                            alertDialog.setMessage("$translatedText")
                            alertDialog.show()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            }
            builder.create().show()

        }

        subjectTextView.text = currentItem.subjectName
        homeworkTextView.text = currentItem.homeworkName
        studentTextView.text = currentItem.studentNameWithId
        reviewTextView.text=currentItem.review

        if(currentItem.parentReply.equals(""))
        {
            giveReply.text="Give Reply"
        }
        else
        {
            giveReply.text="View Reply"
        }

        return itemView
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

data class CustomListItem(

    val homeWorkId:String,
    val subjectName: String,
    val homeworkName: String,
    val studentNameWithId: String,
    val review:String,
    val parentReply:String,
    val teacherId:String
)

