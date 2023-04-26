package com.example.asaanassessment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.anychart.AnyChartView


class ViewProgressTeacherFragment : Fragment(),AdapterView.OnItemSelectedListener {
    var StudentIndexSelected:Int = -1
    var SubjectIndexSelected:Int=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_progress_teacher, container, false)


        val autoCompleteTextViewSubject: AutoCompleteTextView = view.findViewById(R.id.teacher_subject_selection)
        val items = listOf("English", "Maths", "Physics")

// Create an ArrayAdapter with the items and set it to the AutoCompleteTextView
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        autoCompleteTextViewSubject.setAdapter(adapter)

// Add an item click listener to the AutoCompleteTextView
        autoCompleteTextViewSubject.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            Toast.makeText(requireContext(), "Selected item: $selectedItem", Toast.LENGTH_SHORT)
                .show()


        }



        val autoCompleteTextViewStudent: AutoCompleteTextView = view.findViewById(R.id.teacher_student_selection)
        val student = listOf("Hafeez", "Rauf", "Qudoos")

// Create an ArrayAdapter with the items and set it to the AutoCompleteTextView
        val adapterStudent = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, student)
        autoCompleteTextViewStudent.setAdapter(adapterStudent)

// Add an item click listener to the AutoCompleteTextView
        autoCompleteTextViewStudent.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String
            Toast.makeText(requireContext(), "Selected Student: $selectedItem", Toast.LENGTH_SHORT)
                .show()


        }


        return view
        //
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        when(p0?.id) {
            R.id.teacher_student_selection-> {
                StudentIndexSelected = p2

            }
            R.id.teacher_subject_selection -> {
                SubjectIndexSelected = p2
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

}