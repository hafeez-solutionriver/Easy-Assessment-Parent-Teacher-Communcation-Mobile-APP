package com.example.asaanassessment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat


class EnrolledStudentsTeacherFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.fragment_enrolled_students_teacher, container, false)

        val autoCompleteTextViewSubject: AutoCompleteTextView =
            view.findViewById(R.id.teacher_subject_selection)
        val items = listOf("English", "Maths", "Physics")

// Create an ArrayAdapter with the items and set it to the AutoCompleteTextView
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        autoCompleteTextViewSubject.setAdapter(adapter)
        val listView = view.findViewById<ListView>(R.id.list_view_enrolledstudents)
        autoCompleteTextViewSubject.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            // Do something with the selected item



            val itemList = listOf(

                Student("023-19-0006","Hafeez"),
                Student("023-19-0004","Ranjeeta"),
                Student("023-19-0232","Mujtaba"),
                Student("023-19-0006","Hafeez"),
                Student("023-19-0004","Ranjeeta"),
                Student("023-19-0232","Mujtaba"),
                Student("023-19-0006","Hafeez"),
                Student("023-19-0004","Ranjeeta"),
                Student("023-19-0232","Mujtaba"),
                Student("023-19-0006","Hafeez"),
                Student("023-19-0004","Ranjeeta"),
                Student("023-19-0232","Mujtaba"),
                Student("023-19-0006","Hafeez"),
                Student("023-19-0004","Ranjeeta"),
                Student("023-19-0232","Mujtaba"),
                Student("023-19-0006","Hafeez"),
                Student("023-19-0004","Ranjeeta"),
                Student("023-19-0232","Mujtaba"),
                Student("023-19-0006","Hafeez"),
                Student("023-19-0004","Ranjeeta"),
                Student("023-19-0232","Mujtaba"),
                Student("023-19-0006","Hafeez"),
                Student("023-19-0004","Ranjeeta"),
                Student("023-19-0232","Mujtaba"),


                )

            val adapterStd = StudentAdapter(requireContext(), itemList)
            listView.adapter = adapterStd


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
