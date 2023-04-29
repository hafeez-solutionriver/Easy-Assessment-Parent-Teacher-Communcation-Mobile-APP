package com.example.asaanassessment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


class SendReminderTeacherFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_send_reminder_teacher, container, false)

        val autoCompleteTextViewSubject: AutoCompleteTextView =
            view.findViewById(R.id.teacher_subject_selection)
        val items = listOf("English", "Maths", "Physics")

// Create an ArrayAdapter with the items and set it to the AutoCompleteTextView
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        autoCompleteTextViewSubject.setAdapter(adapter)

        val sendReminderBtn = view.findViewById<Button>(R.id.sendreminder)

        sendReminderBtn.setOnClickListener {

            val title = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.notificationtitle).editText?.text.toString()

            val description =  view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.notificationdescription).editText?.text.toString()


            val alertDialog = AlertDialog.Builder(requireContext()).create()
            alertDialog.setTitle("Reminder")
            alertDialog.setMessage(title+"\n"+description)
            alertDialog.show()

        }
        return view

    }


}