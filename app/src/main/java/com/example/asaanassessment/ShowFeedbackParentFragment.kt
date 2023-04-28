package com.example.asaanassessment


import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions

class ShowFeedbackParentFragment() : Fragment(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().actionBar?.title="Show Feedback"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_show_feedback_parent, container, false)


        val listView = view.findViewById<ListView>(R.id.list_view_parent_showFeedback)

        val itemList = listOf(
            CustomListItem("Maths", "Chapter 1", "John (123)", "Great homework, very helpful! Great homework, very helpful! Great homework, very helpful! Great homework, very helpful! Great homework, very helpful!" ),
            CustomListItem("Science", "Experiment 3", "Emma (234)", "Interesting experiment, enjoyed doing it."),
            CustomListItem("English", "Essay Writing", "David (345)", "Tough assignment, but learned a lot."),
            CustomListItem("Social Studies", "Project Work", "Sophie (456)", "Enjoyed researching and presenting."),
            CustomListItem("Maths", "Chapter 1", "John (123)", "Good practice problems."),
            CustomListItem("Science", "Exp# 3", "Emma (234)", "Could have used more detailed instructions."),
            CustomListItem("English", "Essay Writing", "David (345)", "Confusing prompt, had to clarify with teacher.Confusing prompt, had to clarify with teacherConfusing prompt, had to clarify with teacherConfusing prompt, had to clarify with teacher"),
            CustomListItem("Social Studies", "Project Work", "Sophie (456)", "Fun project, got to work with classmates."),
            CustomListItem("Maths", "Chapter 1", "John (123)", "Clear explanations, understood the concept well."),
            CustomListItem("Science", "Exp# 3", "Emma (234)", "Messy experiment, hard to keep track of materials."),
            CustomListItem("English", "Essay Writing", "David (345)", "Interesting topic, enjoyed writing about it."),
            CustomListItem("Social Studies", "Project Work", "Sophie (456)", "Group work was challenging but rewarding.")
        )

        val adapter = CustomListAdapter(requireContext(), itemList)
        listView.adapter = adapter






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


        val btn = itemView.findViewById<Button>(R.id.translate_button)

        val giveReply = itemView.findViewById<Button>(R.id.reply_button)

        giveReply.setOnClickListener {

            if(giveReply.text.equals("Give Reply"))
            {
                val dialog = Dialog(context)
                dialog.setContentView(R.layout.give_reply_dialog)

                val sendButton = dialog.findViewById<Button>(R.id.send_button)
                val textField = dialog.findViewById<EditText>(R.id.text_field)

                sendButton.setOnClickListener {
                    val reply = textField.text.toString()
                    // now store this reply firebase

                    giveReply.setText("View Reply")


                    dialog.dismiss() // Dismiss the dialog box after the operation is completed
                }

                dialog.show()
            }
            else
            {
                val alertDialog = AlertDialog.Builder(context).create()
                alertDialog.setTitle("Your Reply")
                alertDialog.setMessage("Teacher Reply would be shown here....")
                alertDialog.show()
            }

        }



        btn.setOnClickListener {


            val review = currentItem.review

            // Create an AlertDialog
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Select your language")
            builder.setIcon(R.drawable.baseline_language_24)
            builder.setItems(arrayOf("Sindhi", "Urdu")) { dialog, which ->
                // Get the selected language
                val language = when (which) {
                    0 -> "sd"
                    1 -> "ur"
                    else -> ""
                }

                // Create a progress dialog
                val progressDialog = ProgressDialog(context)
                progressDialog.setMessage("Translating...")
                progressDialog.show()

                // Run the network call on a separate thread
                Thread {
                    try {
                        // Create a Translate client.
                        val options = TranslateOptions.newBuilder()
                            .setApiKey("AIzaSyAjzqPvfa_73xPMjJTHnTbFAr8IQjn9HU8")
                            .build()
                        val translate = options.service

                        // Translate the text
                        val translation = translate.translate(
                            review,
                            Translate.TranslateOption.sourceLanguage("en"),
                            Translate.TranslateOption.targetLanguage(language)
                        )
                        val translatedText = translation.translatedText

                        // Update the UI on the main thread
                        it.post {
                            // Dismiss the progress dialog
                            progressDialog.dismiss()

                            // Display the translated text
                            val alertDialog = AlertDialog.Builder(context).create()
                            alertDialog.setTitle("Review")
                            alertDialog.setMessage("$translatedText")
                            alertDialog.show()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            }
            builder.create().show()

        }

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

