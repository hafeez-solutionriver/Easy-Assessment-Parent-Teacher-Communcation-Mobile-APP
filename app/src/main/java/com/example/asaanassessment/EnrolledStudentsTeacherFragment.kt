package com.example.asaanassessment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.LinkedList


class EnrolledStudentsTeacherFragment(val teacher:String) : Fragment() {

    private lateinit var mContext: Context
    var SubjectIndexSelected: Int = -1
    lateinit var subjectIds: MutableList<String>
    lateinit var subjectNames: MutableList<String>
    lateinit var studentIds: MutableList<String>
    lateinit var studentNames: MutableList<String>

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
       val view = inflater.inflate(R.layout.fragment_enrolled_students_teacher, container, false)

        val autoCompleteTextViewSubject: AutoCompleteTextView =
            view.findViewById(R.id.teacher_subject_selection)




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




        val listView = view.findViewById<ListView>(R.id.list_view_enrolledstudents)
        autoCompleteTextViewSubject.setOnItemClickListener { parent, view, position, id ->

        SubjectIndexSelected =position

            val studentRef = database.getReference("Student")



            studentRef.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {


                    studentNames = mutableListOf<String>()
                    studentIds = mutableListOf<String>()

                    for (studentSnapshot in dataSnapshot.children) {
                        val studentName =
                            studentSnapshot.child("FirstName").getValue(String::class.java)

                        if (studentName != null) {

                            var isSameSubject=false

                            var index=0
                            for(subject in studentSnapshot.child("Subjects").children)
                            {
                                if(subject.child((index++.toString())).getValue(String::class.java).equals(subjectIds[SubjectIndexSelected])){
                                    isSameSubject=true
                                    break;
                                }
                            }

                            if(!isSameSubject)
                            {
                                studentNames.add(studentName)
                                studentIds.add(studentSnapshot.key.toString())
                            }



                        }
                    }

                    val items = LinkedList<Student>()

                    for (index in studentNames.indices) {
                        items.add(Student(studentIds[index],studentNames[index]))
                    }



                    val adapterStudent =
                        StudentAdapter(mContext, items)
                    listView.setAdapter(adapterStudent)
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






        return view

    }


}
class StudentAdapter(private val context: Context, private val data: List<Student>) :
    ArrayAdapter<Student>(context, R.layout.custom_list_view_enrolledstudents, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.custom_list_view_enrolledstudents, parent, false)


        itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.subject))

        val stdID = itemView.findViewById<TextView>(R.id.student_id)

        val stdName = itemView.findViewById<TextView>(R.id.student_name)
        val currentItem = data[position]

        stdID.text = currentItem.stdID
        stdName.text=currentItem.stdName

        return itemView
    }
}

data class Student(
    val stdID: String,
    val stdName:String

)
