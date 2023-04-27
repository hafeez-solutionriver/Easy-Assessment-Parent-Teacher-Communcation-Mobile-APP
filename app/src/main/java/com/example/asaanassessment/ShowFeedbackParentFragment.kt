package com.example.asaanassessment



import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog


class ShowFeedbackParentFragment : Fragment(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_show_feedback_parent, container, false)


        val listView = view.findViewById<ListView>(R.id.list_view_parent_showFeedback)

        val itemList = listOf(
            CustomListItem("Maths", "Chapter 1", "John (123)", "Great homework, very helpful!"),
            CustomListItem("Science", "Experiment 3", "Emma (234)", "Interesting experiment, enjoyed doing it."),
            CustomListItem("English", "Essay Writing", "David (345)", "Tough assignment, but learned a lot."),
            CustomListItem("Social Studies", "Project Work", "Sophie (456)", "Enjoyed researching and presenting."),
            CustomListItem("Maths", "Chapter 1", "John (123)", "Good practice problems."),
            CustomListItem("Science", "Exp# 3", "Emma (234)", "Could have used more detailed instructions."),
            CustomListItem("English", "Essay Writing", "David (345)", "Confusing prompt, had to clarify with teacher."),
            CustomListItem("Social Studies", "Project Work", "Sophie (456)", "Fun project, got to work with classmates."),
            CustomListItem("Maths", "Chapter 1", "John (123)", "Clear explanations, understood the concept well."),
            CustomListItem("Science", "Exp# 3", "Emma (234)", "Messy experiment, hard to keep track of materials."),
            CustomListItem("English", "Essay Writing", "David (345)", "Interesting topic, enjoyed writing about it."),
            CustomListItem("Social Studies", "Project Work", "Sophie (456)", "Group work was challenging but rewarding.")
        )

        val adapter = CustomListAdapter(requireContext(), itemList)
        listView.adapter = adapter



        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = listView.getItemAtPosition(position) as CustomListItem
                val review = selectedItem.review

                val alertDialog = AlertDialog.Builder(requireContext()).create()
                alertDialog.setTitle("Review")
                alertDialog.setMessage(review)


                alertDialog.show()
            }
        return view
        //
    }


}

class CustomListAdapter(private val context: Context, private val data: List<CustomListItem>) :
    ArrayAdapter<CustomListItem>(context, R.layout.custom_list_view_parent, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.custom_list_view_parent, parent, false)

        val subjectTextView = itemView.findViewById<TextView>(R.id.subject_name)
        val homeworkTextView = itemView.findViewById<TextView>(R.id.homework_name)
        val studentTextView = itemView.findViewById<TextView>(R.id.student_name_with_id)
        val reviewTextView = itemView.findViewById<TextView>(R.id.review)
        val currentItem = data[position]

        subjectTextView.text = currentItem.subjectName
        homeworkTextView.text = currentItem.homeworkName
        studentTextView.text = currentItem.studentNameWithId
            reviewTextView.text=currentItem.review
        return itemView
    }
}

data class CustomListItem(
    val subjectName: String,
    val homeworkName: String,
    val studentNameWithId: String,
    val review:String
)

