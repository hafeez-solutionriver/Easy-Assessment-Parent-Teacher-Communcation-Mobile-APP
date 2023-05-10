package com.example.asaanassessment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*


class NotificationParentFragment(var parentId:String) : Fragment() {

    lateinit var senders: MutableList<String>
    lateinit var dates: MutableList<String>
    lateinit var times: MutableList<String>
    lateinit var titles: MutableList<String>
    lateinit var descriptions: MutableList<String>
    lateinit var isSeenList: MutableList<Boolean>
    lateinit var isReminderList: MutableList<Boolean>
    lateinit var notificationIds: MutableList<String>

    private lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().actionBar?.title="Notification"
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
       val view =  inflater.inflate(R.layout.fragment_notification_parent, container, false)
        val listView = view.findViewById<ListView>(R.id.list_view_parent_notification)

        val database = FirebaseDatabase.getInstance()
        val notificationRef = database.getReference("Parent/$parentId/Notification")

        notificationRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {


                senders = mutableListOf<String>()
                notificationIds = mutableListOf<String>()
                dates =  mutableListOf<String>()
                times= mutableListOf<String>()
                titles=  mutableListOf<String>()
                descriptions= mutableListOf<String>()
                isSeenList= mutableListOf<Boolean>()
                isReminderList=mutableListOf<Boolean>()

                for (notificationSnapshot in dataSnapshot.children) {

                    notificationIds.add(notificationSnapshot.key.toString())
                    senders.add(notificationSnapshot.child("sender").getValue(String::class.java).toString())
                    dates.add(notificationSnapshot.child("date").getValue(String::class.java).toString())
                    times.add(notificationSnapshot.child("time").getValue(String::class.java).toString())
                    titles.add(notificationSnapshot.child("title").getValue(String::class.java).toString())
                    descriptions.add(notificationSnapshot.child("description").getValue(String::class.java).toString())
                    isSeenList.add(notificationSnapshot.child("isSeen").getValue(Boolean::class.java)!!)
                    isReminderList.add(notificationSnapshot.child("isReminder").getValue(Boolean::class.java)!! )

                }

                val items = mutableListOf<NotificationParent>()

                for (index in notificationIds.indices) {
                    items.add(NotificationParent(notificationIds[index],senders[index],dates[index],times[index],titles[index],descriptions[index],isSeenList[index],isReminderList[index]))
                }

                val adapter = NotificationAdapterParent(mContext,items)
                listView.adapter = adapter


                listView.setOnItemClickListener { parent, view, position, id ->
                    val currentItem = adapter.getItem(position)

                    currentItem?.isSeen=true
                    val parentReplyRef = database.getReference("Parent/$parentId/Notification/${currentItem?.notificationId}/isSeen")

                    // Set the value for parentReply
                    parentReplyRef.setValue(true).addOnCompleteListener {

                        if (it.isSuccessful) {



                        }
                    }


                    adapter.notifyDataSetChanged()

                    if(currentItem?.sender.equals("Teacher") && !currentItem?.isReminder!! )
                    {
                        val fragmentManager = requireActivity().supportFragmentManager

                        val ft = fragmentManager.beginTransaction()

                        ft.replace(R.id.parent_fragment_container, ShowFeedbackParentFragment(parentId))

                        ft.commit()
                    }

                    if(currentItem?.isReminder!!)
                    {
                        val alertDialog = AlertDialog.Builder(mContext).create()
                        alertDialog.setTitle(currentItem.title)
                        alertDialog.setMessage(currentItem.description)
                        alertDialog.show()
                    }
                    // set background color to light blue to indicate seen
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(mContext, databaseError.toException().toString(), Toast.LENGTH_SHORT)
            }
        })



        return view
    }


}
data class NotificationParent(
    var notificationId:String,
    val sender: String,
    val date: String,
    val time: String,
    val title: String,
    val description: String,
    var isSeen: Boolean ,
    var isReminder:Boolean

)

class NotificationAdapterParent(private val context: Context, private val data: List<NotificationParent>) :
    ArrayAdapter<NotificationParent>(context, R.layout.notification_custom_view, data) {

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
        if (!currentItem.isSeen) {
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
        } else {
            itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.grey))
        }
        return itemView
    }
}