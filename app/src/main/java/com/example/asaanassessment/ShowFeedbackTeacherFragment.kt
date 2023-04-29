package com.example.asaanassessment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog



class ShowFeedbackTeacherFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_show_feedback_teacher, container, false)

        val listView = view.findViewById<ListView>(R.id.list_view_teacher_showFeedback)

        val itemList = listOf(
            CustomListItemParentFeedback("Maths", "Chapter 1", "John (123)", "Great homework, very helpful! Great homework, very helpful! Great homework, very helpful! Great homework, very helpful! Great homework, very helpful!" ,"skdhf"),
            CustomListItemParentFeedback("Science", "Experiment 3", "Emma (234)", "Interesting experiment, enjoyed doing it.","skdhf"),
            CustomListItemParentFeedback("English", "Essay Writing", "David (345)", "Tough assignment, but learned a lot.","skdhf"),
            CustomListItemParentFeedback("Social Studies", "Project Work", "Sophie (456)", "Enjoyed researching and presenting.","skdhf"),
            CustomListItemParentFeedback("Maths", "Chapter 1", "John (123)", "Good practice problems.",""),
            CustomListItemParentFeedback("Science", "Exp# 3", "Emma (234)", "Could have used more detailed instructions.",""),
            CustomListItemParentFeedback("English", "Essay Writing", "David (345)", "Confusing prompt, had to clarify with teacher.Confusing prompt, had to clarify with teacherConfusing prompt, had to clarify with teacherConfusing prompt, had to clarify with teacher",""),
            CustomListItemParentFeedback("Social Studies", "Project Work", "Sophie (456)", "Fun project, got to work with classmates.",""),
            CustomListItemParentFeedback("Maths", "Chapter 1", "John (123)", "Clear explanations, understood the concept well.","skdhf"),
            CustomListItemParentFeedback("Science", "Exp# 3", "Emma (234)", "Messy experiment, hard to keep track of materials.","skdhf"),
            CustomListItemParentFeedback("English", "Essay Writing", "David (345)", "Interesting topic, enjoyed writing about it.","skdhf"),
            CustomListItemParentFeedback("Social Studies", "Project Work", "Sophie (456)", "Group work was challenging but rewarding.","")
        )

        val adapter = CustomListAdapterParentFeedback(requireContext(), itemList)
        listView.adapter = adapter

        return view
        //
    }


}

class CustomListAdapterParentFeedback(private val context: Context, private val data: List<CustomListItemParentFeedback>) :
    ArrayAdapter<CustomListItemParentFeedback>(context, R.layout.custom_list_view_teacher_showfeedback, data) {

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

data class CustomListItemParentFeedback(
    val subjectName: String,
    val homeworkName: String,
    val studentNameWithId: String,
    val review:String,
    val parentReply:String
)
