package com.example.asaanassessment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*



class NotificationTeacherFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_notification_teacher, container, false)


        val listView = view.findViewById<ListView>(R.id.list_view_teacher_notification)

        val notifications = listOf(
            NotificationTeacher("Admin", getCurrentDate(), getCurrentTime(), "Title 1", "Description 1",false),
            NotificationTeacher("Teacher", getCurrentDate(), getCurrentTime(), "Title 2", "Description 2",true),
            NotificationTeacher("Admin", getCurrentDate(), getCurrentTime(), "Title 3", "Description 3",true),
            NotificationTeacher("Teacher", getCurrentDate(), getCurrentTime(), "Title 4", "Description 4",false),
            NotificationTeacher("Admin", getCurrentDate(), getCurrentTime(), "Title 5", "Description 5",true),
            NotificationTeacher("Teacher", getCurrentDate(), getCurrentTime(), "Title 6", "Description 6",false),
            NotificationTeacher("Admin", getCurrentDate(), getCurrentTime(), "Title 7", "Description 7",true),
            NotificationTeacher("Teacher", getCurrentDate(), getCurrentTime(), "Title 8", "Description 8",false),
            NotificationTeacher("Admin", getCurrentDate(), getCurrentTime(), "Title 9", "Description 9",false),
            NotificationTeacher("Teacher", getCurrentDate(), getCurrentTime(), "Title 10", "Description 10",true)
        )

        val adapter = NotificationAdapterTeacher(requireContext(),notifications)
        listView.adapter = adapter



        listView.setOnItemClickListener { parent, view, position, id ->
            val currentItem = adapter.getItem(position)

            currentItem?.isSeen=true
            adapter.notifyDataSetChanged()
            // set background color to light blue to indicate seen
        }

        return view
    }
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    private fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val currentTime = Date()
        return timeFormat.format(currentTime)
    }


}
data class NotificationTeacher(
    val sender: String,
    val date: String,
    val time: String,
    val title: String,
    val description: String,
    var isSeen: Boolean = false
)

class NotificationAdapterTeacher(private val context: Context, private val data: List<NotificationTeacher>) :
    ArrayAdapter<NotificationTeacher>(context, R.layout.notification_custom_view, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.notification_custom_view, parent, false)

        val senderTextView = itemView.findViewById<TextView>(R.id.sender)
        val dateTextView = itemView.findViewById<TextView>(R.id.date)
        val timeTextView = itemView.findViewById<TextView>(R.id.time)
        val titleTextView = itemView.findViewById<TextView>(R.id.title)
        val descriptionTextView = itemView.findViewById<TextView>(R.id.description)

        val currentItem = data[position]

        senderTextView.text = currentItem.sender
        dateTextView.text = currentItem.date
        timeTextView.text = currentItem.time
        titleTextView.text = currentItem.title
        descriptionTextView.text = currentItem.description

        // Change background color based on isSeen value
        if (currentItem.isSeen) {
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
        } else {
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.grey))
        }

        return itemView
    }

}