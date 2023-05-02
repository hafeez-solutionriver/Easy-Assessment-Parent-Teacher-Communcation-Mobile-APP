package com.example.asaanassessment

import android.animation.ObjectAnimator
import android.app.ProgressDialog
import android.os.Bundle
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


class ProvideFeedbackTeacherFragment(val teacher:String) : Fragment() {
    var StudentIndexSelected: Int = -1
    var SubjectIndexSelected: Int = -1
    var startRating:Float= 0.0F
    lateinit var subjectIds: MutableList<String>
    lateinit var subjectNames: MutableList<String>

    lateinit var studentIds: MutableList<String>
    lateinit var studentNames: MutableList<String>


    lateinit var studentParentIds: MutableList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view  = inflater.inflate(R.layout.fragment_provide_feedback_teacher, container, false)


        val autoCompleteTextViewSubject: AutoCompleteTextView =
            view.findViewById(R.id.teacher_subject_selection_feedback)

        val database = FirebaseDatabase.getInstance()
        val subjectsRef = database.getReference("Subject")



        subjectsRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {


            subjectNames= mutableListOf<String>()
                subjectIds= mutableListOf<String>()
                for (subjectSnapshot in dataSnapshot.children) {
                    val subjectName = subjectSnapshot.child("CourseName").getValue(String::class.java)

                    if (subjectName != null) {

                        subjectNames.add(subjectName)
                        subjectIds.add(subjectSnapshot.key.toString())
                    }
                }

                val items =  mutableListOf<String>()

                for(index in subjectNames.indices)
                {
                    items.add(subjectNames[index]+"  -  "+subjectIds[index])
                }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
                autoCompleteTextViewSubject.setAdapter(adapter)
            }

            override fun onCancelled(databaseError: DatabaseError) {
               Toast.makeText(requireContext(),databaseError.toException().toString(),Toast.LENGTH_SHORT)
            }


        }


        )


        autoCompleteTextViewSubject.setOnItemClickListener { parent, view, position, id ->

            SubjectIndexSelected= position

            val studentRef = database.getReference("Student")



            studentRef.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {


                    studentNames= mutableListOf<String>()
                    studentIds= mutableListOf<String>()
                    studentParentIds= mutableListOf<String>()
                    for (studentSnapshot in dataSnapshot.children) {
                        val studentName = studentSnapshot.child("FirstName").getValue(String::class.java)

                        if (studentName != null) {

                            studentNames.add(studentName)
                            studentIds.add(studentSnapshot.key.toString())
                            studentParentIds.add(studentSnapshot.child("ParentId").getValue(String::class.java).toString())
                        }
                    }

                    val items =  mutableListOf<String>()

                    for(index in studentNames.indices)
                    {
                        items.add(studentNames[index]+"  -  "+studentIds[index])
                    }
                    val autoCompleteTextViewStudent: AutoCompleteTextView =
                        requireView().findViewById(R.id.teacher_student_selection_feedback)

                    autoCompleteTextViewStudent.onItemSelectedListener

                       autoCompleteTextViewStudent.setOnItemClickListener { parent, view, position, id ->

                           StudentIndexSelected = position
                       }
// Create an ArrayAdapter with the items and set it to the AutoCompleteTextView
                    val adapterStudent =
                        ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
                    autoCompleteTextViewStudent.setAdapter(adapterStudent)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(requireContext(),databaseError.toException().toString(),Toast.LENGTH_SHORT)
                }


            }


            )


        }







// Add an item click listener to the AutoCompleteTextView

        val ratingBar = view.findViewById<me.zhanghai.android.materialratingbar.MaterialRatingBar>(R.id.rating)

        val send = view.findViewById<Button>(R.id.submit_button)




        send.setOnClickListener {

            if (StudentIndexSelected != -1 && SubjectIndexSelected != -1) {

                val review = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.teacherReviewField).editText?.text.toString()
                val subjectId = subjectIds[SubjectIndexSelected]
                val parentId = studentParentIds[StudentIndexSelected]
                val studentId  = studentIds[StudentIndexSelected]
                val assignmentName = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.teacherAssignmentNameField).editText?.text.toString()

                if (review.isEmpty()) {
                    Toast.makeText(requireContext(), "Please write a review!", Toast.LENGTH_SHORT).show()
                } else if (ratingBar.rating <= 0.0) {
                    Toast.makeText(requireContext(), "Please select rating!", Toast.LENGTH_SHORT).show()
                } else {

                    val progressDialog = ProgressDialog.show(
                        requireContext(), // context
                        "Sending", // title
                        "Loading. Please wait...", // message
                        true // indeterminate
                    )
                    // Query the Homework path for existing reviews with the same subject, student, and assignment
                    val homeworkRef = FirebaseDatabase.getInstance().getReference("Homework")
                    homeworkRef.orderByChild("subjectId").equalTo(subjectId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var reviewExists = false
                            for (snapshot in dataSnapshot.children) {
                                val existingHomework = snapshot.getValue(Homework::class.java)
                                if (existingHomework != null && existingHomework.studentId == studentId && existingHomework.assignmentName == assignmentName && existingHomework.subjectId==subjectId ) {
                                    reviewExists = true
                                    break
                                }
                            }

                            if (reviewExists) {
                                Toast.makeText(requireContext(), "You have already reviewed this assignment for this student!", Toast.LENGTH_SHORT).show()
                                progressDialog.dismiss()
                            } else {


                                // Generate a unique key for the homework node
                                val homeworkKey = homeworkRef.push().key!!

                                // Create a new homework object with the given fields
                                val homework = HashMap<String, Any>()
                                homework["subjectId"] = subjectId
                                homework["parentId"] = parentId
                                homework["studentId"] = studentId
                                homework["assignmentName"] = assignmentName
                                homework["review"] = review
                                homework["rating"] = ratingBar.rating
                                homework["parentReply"] = ""

                                // Put the homework object at the generated key
                                homeworkRef.child(homeworkKey).setValue(homework)

                                progressDialog.dismiss()

                                view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.teacherReviewField).editText?.text?.clear()


                                Toast.makeText(requireContext(),"Successfully sent.",Toast.LENGTH_SHORT).show()



                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(requireContext(), "Error: " + databaseError.message, Toast.LENGTH_SHORT).show()
progressDialog.dismiss()
                        }
                    })
                }
            }

            else
                Toast.makeText(requireContext(),"Please select a student!",Toast.LENGTH_SHORT).show()



        }





        return view
    }

    fun updateSubject(teacherId:String)
    {
        val autoCompleteTextViewSubject: AutoCompleteTextView =
            requireView().findViewById(R.id.teacher_subject_selection_feedback)



        val items = listOf("English", "Maths", "Physics")

// Create an ArrayAdapter with the items and set it to the AutoCompleteTextView
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        autoCompleteTextViewSubject.setAdapter(adapter)

// Add an item click listener to the AutoCompleteTextView
        autoCompleteTextViewSubject.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String


            SubjectIndexSelected = position

            if (StudentIndexSelected != -1 && SubjectIndexSelected != -1) {


            }

        }
    }
}
class Homework (
    var subjectId: String = "",
    var parentId: String = "",
    var studentId: String = "",
    var assignmentName: String = "",
    var review: String = "",
    var rating: Float = 0f,
    var parentReply: String = ""
)
