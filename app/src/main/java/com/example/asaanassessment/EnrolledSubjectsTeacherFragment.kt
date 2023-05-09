package com.example.asaanassessment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.LinkedList


class EnrolledSubjectsTeacherFragment(val teacher:String) : Fragment() {
    private lateinit var mContext: Context

    lateinit var subjectIds: MutableList<String>
    lateinit var subjectNames: MutableList<String>

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

        val view =  inflater.inflate(R.layout.fragment_enrolled_subjects_teacher, container, false)

        val listView = view.findViewById<ListView>(R.id.list_view_enrolledsubjects)

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

                val items = LinkedList<Subject>()

                for (index in subjectNames.indices) {
                    items.add(Subject(subjectNames[index]))
                }
                val adapter =
                    SubjectAdapter(mContext,items)
                listView.setAdapter(adapter)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(mContext, databaseError.toException().toString(), Toast.LENGTH_SHORT)
            }


        }


        )



   return view
    }


}

class SubjectAdapter(private val context: Context, private val data: List<Subject>) :
    ArrayAdapter<Subject>(context, R.layout.custom_list_view_enrolledsubjects, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.custom_list_view_enrolledsubjects, parent, false)


        itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.subject))

        val subjectTextView = itemView.findViewById<TextView>(R.id.subject_name)

        val currentItem = data[position]

        subjectTextView.text = currentItem.subjectName

        return itemView
    }
}

data class Subject(
    val subjectName: String

)
