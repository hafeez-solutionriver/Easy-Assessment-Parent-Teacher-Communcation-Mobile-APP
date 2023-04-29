package com.example.asaanassessment

import android.animation.ObjectAnimator
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


class ProvideFeedbackTeacherFragment : Fragment() {
    var StudentIndexSelected: Int = -1
    var SubjectIndexSelected: Int = -1
    var startRating:Float= 0.0F

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


        val autoCompleteTextViewStudent: AutoCompleteTextView =
            view.findViewById(R.id.teacher_student_selection_feedback)
        val student = listOf("Hafeez", "Rauf", "Qudoos")

// Create an ArrayAdapter with the items and set it to the AutoCompleteTextView
        val adapterStudent =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, student)
        autoCompleteTextViewStudent.setAdapter(adapterStudent)

// Add an item click listener to the AutoCompleteTextView
        autoCompleteTextViewStudent.setOnItemClickListener { parent, view, position, id ->

            StudentIndexSelected = position

            if (StudentIndexSelected != -1 && SubjectIndexSelected != -1) {


            }

        }
        val ratingBar = view.findViewById<me.zhanghai.android.materialratingbar.MaterialRatingBar>(R.id.rating)

        val send = view.findViewById<Button>(R.id.submit_button)




        send.setOnClickListener {

            if (StudentIndexSelected != -1 && SubjectIndexSelected != -1) {


                val review = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.teacherReviewField)

                val alertDialog = AlertDialog.Builder(requireContext()).create()
                alertDialog.setTitle("Review & rating")
                alertDialog.setMessage(review.editText?.text.toString()+":"+ratingBar.rating)
                alertDialog.show()

            }

        }



        return view
    }


}