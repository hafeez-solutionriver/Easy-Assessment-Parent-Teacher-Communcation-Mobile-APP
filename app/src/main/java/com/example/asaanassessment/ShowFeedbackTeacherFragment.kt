package com.example.asaanassessment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.anychart.AnyChartView


class ShowFeedbackTeacherFragment : Fragment(),AdapterView.OnItemSelectedListener {
    var StudentIndexSelected:Int = -1
    var SubjectIndexSelected:Int=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_show_feedback_teacher, container, false)

        val studentSpinner: Spinner = view.findViewById(R.id.student_selection_teacher)
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            view.context,
            R.array.programming_languages,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            studentSpinner.adapter = adapter
        }

        studentSpinner.onItemSelectedListener = this



        val subjectSpinner: Spinner = view.findViewById(R.id.subject_selection_teacher)
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            view.context,
            R.array.programming_languages,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            subjectSpinner.adapter = adapter
        }

        subjectSpinner.onItemSelectedListener = this


        return view
        //
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        when(p0?.id) {
            R.id.student_spinner -> {
                StudentIndexSelected = p2
            }
            R.id.subject_spinner -> {
                SubjectIndexSelected = p2
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

}