package com.example.asaanassessment


import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ShowFeedbackTeacherFragment(val teacher:String) : Fragment() {

    lateinit var subjectNames: MutableList<String>
    lateinit var assignmentNames: MutableList<String>
    lateinit var studentIds: MutableList<String>
    lateinit var studentNames: MutableList<String>
    lateinit var reviews: MutableList<String>
    lateinit var parentsReply: MutableList<String>
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
        val view = inflater.inflate(R.layout.fragment_show_feedback_teacher, container, false)

        val listView = view.findViewById<ListView>(R.id.list_view_teacher_showFeedback)

        val database = FirebaseDatabase.getInstance()

        val homeworkRef = database.getReference("Homework")

        val progressDialog = ProgressDialog(mContext)
        progressDialog.setMessage("Fetching data...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        homeworkRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                progressDialog.dismiss()

                subjectNames = mutableListOf<String>()
                assignmentNames = mutableListOf<String>()
                studentIds = mutableListOf<String>()
                studentNames = mutableListOf<String>()
                reviews = mutableListOf<String>()
                parentsReply = mutableListOf<String>()

                for (homeworkSnapshot in dataSnapshot.children) {

                    val databaseTeacherId =
                        homeworkSnapshot.child("teacherId").getValue(String::class.java)

                    if (databaseTeacherId != null && databaseTeacherId.equals(
                            teacher
                        )
                    ) {

                        studentNames.add(
                            homeworkSnapshot.child("studentName").getValue(String::class.java)
                                .toString()
                        )
                        studentIds.add(
                            homeworkSnapshot.child("studentId").getValue(String::class.java)
                                .toString()
                        )
                        assignmentNames.add(
                            homeworkSnapshot.child("assignmentName")
                                .getValue(String::class.java)
                                .toString()
                        )
                        reviews.add(
                            homeworkSnapshot.child("review").getValue(String::class.java)
                                .toString()
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

                val items = mutableListOf<CustomListItemTeacherFeedback>()

                for (index in studentNames.indices) {
                    items.add(
                        CustomListItemTeacherFeedback(
                            subjectNames[index],
                            assignmentNames[index],
                            studentNames[index] + "-" + studentIds[index],
                            reviews[index],
                            parentsReply[index]
                        )
                    )
                }

                val adapter = CustomListAdapterParentFeedback(mContext, items)
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


        //
    }


}

class CustomListAdapterParentFeedback(private val context: Context, private val data: MutableList<CustomListItemTeacherFeedback>) :
    ArrayAdapter<CustomListItemTeacherFeedback>(context, R.layout.custom_list_view_teacher_showfeedback, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.custom_list_view_teacher_showfeedback, parent, false)



        val subjectTextView = itemView.findViewById<TextView>(R.id.subject_name)
        val homeworkTextView = itemView.findViewById<TextView>(R.id.homework_name)
        val studentTextView = itemView.findViewById<TextView>(R.id.student_name_with_id)
        val reviewTextView = itemView.findViewById<TextView>(R.id.review)
        val currentItem = data[position]




        val viewReply = itemView.findViewById<Button>(R.id.reply_button)
        if (currentItem.parentReply.isEmpty()) {
            viewReply.isEnabled = false
        } else {
            viewReply.isEnabled = true
            viewReply.setOnClickListener {
                val alertDialog = AlertDialog.Builder(context).create()
                alertDialog.setTitle("Parent reply:")
                alertDialog.setMessage(currentItem.parentReply)
                alertDialog.show()
            }
        }




        subjectTextView.text = currentItem.subjectName
        homeworkTextView.text = currentItem.homeworkName
        studentTextView.text = currentItem.studentNameWithId
        reviewTextView.text=currentItem.review
        return itemView
    }
}

data class CustomListItemTeacherFeedback(
    val subjectName: String,
    val homeworkName: String,
    val studentNameWithId: String,
    val review:String,
    val parentReply:String
)
