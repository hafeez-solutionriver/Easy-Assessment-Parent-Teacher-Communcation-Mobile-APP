package com.example.asaanassessment
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase


class Teacher : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {


    lateinit var drawerLayout:DrawerLayout;
    lateinit var navigationView:NavigationView
    lateinit var toolbar: Toolbar
    var teacherId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)

        teacherId=intent.getStringExtra("Id").toString()
        drawerLayout = findViewById(R.id.drawer_teacher)
        navigationView = findViewById(R.id.teacher_navigationView)
        toolbar = findViewById(R.id.toolbar_teacher)



        navigationView.getHeaderView(0).findViewById<TextView>(R.id.header_title).text = intent.getStringExtra("Name").toString()


//        // Create the gradient drawable
//        val gradientDrawable = GradientDrawable(
//            GradientDrawable.Orientation.LEFT_RIGHT, // Set the orientation of the gradient
//            intArrayOf(Color.parseColor("#e74c3c"), Color.parseColor("#2196f3")) // Set the start and end colors
//        )
//
//// Set the corner radius of the drawable (optional)
//        gradientDrawable.cornerRadius = 20f
//
//// Set the background of your view to the gradient drawable
//
//       drawerLayout.background = gradientDrawable

        setSupportActionBar(toolbar)


        var actionbartoogle = ActionBarDrawerToggle(
            this,
            drawerLayout, toolbar, R.string.OpenDrawer, R.string.CloseDrawer
        )

        drawerLayout.addDrawerListener(actionbartoogle)

        actionbartoogle.syncState()

        //Step 2
        navigationView.setNavigationItemSelectedListener(this)





        if(intent.getBooleanExtra("isPushNotification",false))
        {

            val fragmentManager = supportFragmentManager

            val ft = fragmentManager.beginTransaction()


            ft.replace(R.id.teacher_fragment_container, NotificationTeacherFragment(teacherId))

            ft.commit()
        }
        else

        {
            val fragmentManager = supportFragmentManager

            val ft = fragmentManager.beginTransaction()


            ft.replace(R.id.teacher_fragment_container, ProvideFeedbackTeacherFragment(teacherId))

            ft.commit()
        }



    }




    //Step 3
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val fragmentManager = supportFragmentManager

        val ft = fragmentManager.beginTransaction()

        val id = item.itemId

        if (id == R.id.enrolled_students_item_teacher) {


            ft.replace(R.id.teacher_fragment_container, EnrolledStudentsTeacherFragment(teacherId))

        }

        else if (id == R.id.enrolled_subjects_item_teacher) {


            ft.replace(R.id.teacher_fragment_container,EnrolledSubjectsTeacherFragment(teacherId))

        }

        else if (id == R.id.show_feedback_item_teacher) {

            ft.replace(R.id.teacher_fragment_container,ShowFeedbackTeacherFragment(teacherId))


        }

        else if (id == R.id.view_progress_item_teacher) {

            ft.replace(R.id.teacher_fragment_container,ViewProgressTeacherFragment(teacherId))


        }

        else if (id == R.id.send_reminder_item_teacher) {


            ft.replace(R.id.teacher_fragment_container,SendReminderTeacherFragment(teacherId))

        }

        else if (id == R.id.notification_item_teacher) {


            ft.replace(R.id.teacher_fragment_container,NotificationTeacherFragment(teacherId))

        }


        else if (id == R.id.provide_feedback_item_teacher) {


            ft.replace(R.id.teacher_fragment_container,ProvideFeedbackTeacherFragment(teacherId))

        }

        else if (id == R.id.logout_item_teacher) {


            val progressDialog = ProgressDialog.show(
                this, // context
                "Logging out", // title
                "Loading. Please wait...", // message
                true // indeterminate
            )


            val teacherId = intent.getStringExtra("Id").toString()
            val teacherfcm = FirebaseDatabase.getInstance().getReference("Teacher/$teacherId/fcmToken")

            // Set the value for parentReply
            teacherfcm.setValue("").addOnCompleteListener{
                if (it.isSuccessful) {

                    val applicationBasedPref =getSharedPreferences("Teacher", Context.MODE_PRIVATE)
                    val ed = applicationBasedPref.edit()

                    ed.clear()
                    ed.commit()
                    progressDialog.dismiss()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)

                }


                }
            }




        ft.commit()
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            finishAffinity()
        }
    }

}