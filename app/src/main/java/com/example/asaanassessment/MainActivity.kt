package com.example.asaanassessment

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val parent =getSharedPreferences("Parent", Context.MODE_PRIVATE)
        val teacher = getSharedPreferences("Teacher",Context.MODE_PRIVATE)

        if(parent.getString("ParentName",null)!=null) {
            val parentIntent = Intent(this, Parent::class.java)
            parentIntent.putExtra("Name",parent.getString("ParentName",null))


            parentIntent.putExtra("Id",parent.getString("ParentId",null))
            startActivity(parentIntent)

        }

        if(teacher.getString("TeacherName",null)!=null) {


                val teacherIntent = Intent(this, Teacher::class.java)

                teacherIntent.putExtra("Name",teacher.getString("TeacherName",null))
                teacherIntent.putExtra("Id",teacher.getString("TeacherId",null))

                startActivity(teacherIntent)

        }


    }


fun parent(v: View)
{

        val parent = Intent(this, Parent_Log_In::class.java)
        startActivity(parent)



}

    fun teacher(v:View)
    {

            val teacher = Intent(this, Teacher_Log_In::class.java)
            startActivity(teacher)

    }
}

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "channel_id"
        const val CHANNEL_NAME = "Channel Name"
        const val NOTIFICATION_ID = 123
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }



    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
//


        val receiver = remoteMessage.data["receiver"]

        if (receiver == "Teacher") {
            val applicationBasedPref =
                getSharedPreferences("Teacher", Context.MODE_PRIVATE)

            val intent = Intent(this, Teacher::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(
                "Name",
                applicationBasedPref.getString("TeacherName", null)
            )
            intent.putExtra("Id", applicationBasedPref.getString("TeacherId", null))
            intent.putExtra("isPushNotification", true)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(remoteMessage.data["title"])
                .setContentText(remoteMessage.data["body"])
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .build()

            val manager = NotificationManagerCompat.from(applicationContext)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }
            manager.notify(NOTIFICATION_ID, notification)
        } else if (receiver == "Parent") {
            val applicationBasedPref =
                getSharedPreferences("Parent", Context.MODE_PRIVATE)

            val intent = Intent(this, Parent::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(
                "Name",
                applicationBasedPref.getString("ParentName", null)
            )
            intent.putExtra("isPushNotification", true)
            intent.putExtra("Id", applicationBasedPref.getString("ParentId", null))
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(remoteMessage.data["title"])
                .setContentText(remoteMessage.data["body"])
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .build()

            val manager = NotificationManagerCompat.from(applicationContext)
            manager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}




