package com.example.asaanassessment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat


class EnrolledSubjectsTeacherFragment(val teacher:String) : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_enrolled_subjects_teacher, container, false)

        val listView = view.findViewById<ListView>(R.id.list_view_enrolledsubjects)

        val itemList = listOf(
          Subject("Math"),
            Subject("English"),
            Subject("Chemistry")
        )

        val adapter = SubjectAdapter(requireContext(), itemList)
        listView.adapter = adapter

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
