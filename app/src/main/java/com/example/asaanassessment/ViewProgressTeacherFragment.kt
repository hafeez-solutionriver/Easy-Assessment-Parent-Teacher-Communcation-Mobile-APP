package com.example.asaanassessment



import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChartFormat
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.data.Set
import com.anychart.enums.*
import com.anychart.graphics.vector.Stroke
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ViewProgressTeacherFragment(val teacher:String) : Fragment() {

    var StudentIndexSelected: Int = -1
    var SubjectIndexSelected: Int = -1
    private lateinit var mContext: Context

    lateinit var subjectIds: MutableList<String>
    lateinit var subjectNames: MutableList<String>

    lateinit var studentIds: MutableList<String>
    lateinit var studentNames: MutableList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_progress_teacher, container, false)



        val database = FirebaseDatabase.getInstance()
        val subjectsRef = database.getReference("Subject")



        val autoCompleteTextViewSubject: AutoCompleteTextView =
            view.findViewById(R.id.teacher_subject_selection)


        subjectsRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {


                subjectNames = mutableListOf<String>()
                subjectIds = mutableListOf<String>()
                for (subjectSnapshot in dataSnapshot.children) {
                    val subjectName =
                        subjectSnapshot.child("CourseName").getValue(String::class.java)

                    val TeacherID = subjectSnapshot.child("TeacherId").getValue(String::class.java)
                    if (subjectName != null && TeacherID.equals(teacher)) {

                        subjectNames.add(subjectName)
                        subjectIds.add(subjectSnapshot.key.toString())
                    }
                }

                val items = mutableListOf<String>()

                for (index in subjectNames.indices) {
                    items.add(subjectNames[index] + "  -  " + subjectIds[index])
                }
                val adapter =
                    ArrayAdapter(mContext, android.R.layout.simple_dropdown_item_1line, items)
                autoCompleteTextViewSubject.setAdapter(adapter)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(mContext, databaseError.toException().toString(), Toast.LENGTH_SHORT)
            }


        }


        )

// Add an item click listener to the AutoCompleteTextView
        autoCompleteTextViewSubject.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String


            SubjectIndexSelected = position

            if (StudentIndexSelected != -1 && SubjectIndexSelected != -1) {

                displayGraphTeacher(requireView(),subjectIds[SubjectIndexSelected],studentIds[StudentIndexSelected])
            }
            else

            {
                val studentRef = database.getReference("Student")



                studentRef.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {


                        studentNames = mutableListOf<String>()
                        studentIds = mutableListOf<String>()

                        for (studentSnapshot in dataSnapshot.children) {
                            val studentName =
                                studentSnapshot.child("FirstName").getValue(String::class.java)

                            if (studentName != null) {

                                var isSameSubject=false

                                var index=0
                                for(subject in studentSnapshot.child("Subjects").children)
                                {
                                    if(subject.child((index++.toString())).getValue(String::class.java).equals(subjectIds[SubjectIndexSelected])){
                                        isSameSubject=true
                                        break;
                                    }
                                }

                                if(!isSameSubject)
                                {
                                    studentNames.add(studentName)
                                    studentIds.add(studentSnapshot.key.toString())
                                }



                            }
                        }

                        val items = mutableListOf<String>()

                        for (index in studentNames.indices) {
                            items.add(studentNames[index] + "  -  " + studentIds[index])
                        }
                        val autoCompleteTextViewStudent: AutoCompleteTextView =
                            requireView().findViewById(R.id.teacher_student_selection)

                        autoCompleteTextViewStudent.onItemSelectedListener

                        autoCompleteTextViewStudent.setOnItemClickListener { parent, view, position, id ->

                            StudentIndexSelected = position

                            if (StudentIndexSelected != -1 && SubjectIndexSelected != -1) {

                                displayGraphTeacher(requireView(),subjectIds[SubjectIndexSelected],studentIds[StudentIndexSelected])
                            }


                        }
// Create an ArrayAdapter with the items and set it to the AutoCompleteTextView
                        val adapterStudent =
                            ArrayAdapter(mContext, android.R.layout.simple_dropdown_item_1line, items)
                        autoCompleteTextViewStudent.setAdapter(adapterStudent)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(
                            mContext,
                            databaseError.toException().toString(),
                            Toast.LENGTH_SHORT
                        )
                    }


                }


                )
            }

        }

        return view

    }
}



fun displayGraphTeacher(p1: View?, subjectId: String, studentId: String) {

    val anyChartView: AnyChartView = p1!!.findViewById<AnyChartView>(R.id.line_chart_teacher)


    val cartesian = AnyChart.column()


    cartesian.animation(true)

    cartesian.padding(10.0, 10.0, 30.0, 20.0)
    anyChartView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
    // Enable scrolling
//    cartesian.xScroller().enabled(true)
    cartesian.xZoom().setToPointsCount(7, false, null)

    // Allow overlapping labels
    cartesian.xAxis(0).overlapMode(LabelsOverlapMode.ALLOW_OVERLAP)

    cartesian.crosshair().enabled(true)
    cartesian.crosshair()
        .yLabel(true)
        .yStroke(null as Stroke?, null, null, null as String?, null as String?)

    cartesian.tooltip().positionMode(TooltipPositionMode.CHART)

    cartesian.title("Student Progress Chart")

    cartesian.yAxis(0).title("Progress out of 5")
    cartesian.yScale().minimum(0.0)
    cartesian.yScale().maximum(5.0)
    cartesian.yScale().ticks().interval(0.5)

    cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)


    val seriesData: MutableList<DataEntry> = ArrayList()


    val database = FirebaseDatabase.getInstance()
    val homeworkRef = database.getReference("Homework")

    homeworkRef.addValueEventListener(object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {



            for (homeworkSnapshot in dataSnapshot.children) {

                val databaseSubjectId = homeworkSnapshot.child("subjectId").getValue(String::class.java)
                val databaseStudentId = homeworkSnapshot.child("studentId").getValue(String::class.java)
                val databaseRating = homeworkSnapshot.child("rating").getValue(Long::class.java)

                if (databaseSubjectId != null && databaseStudentId != null && databaseRating != null
                    && databaseSubjectId.equals(subjectId) && databaseStudentId.equals(studentId)) {

                    val assignmentName = homeworkSnapshot.child("assignmentName").getValue(String::class.java)

                    if (assignmentName != null) {
                        seriesData.add(StudentProgressDataEntryInTeacher(assignmentName, databaseRating.toDouble()))

                    }
                }
            }

            val set = Set.instantiate()
            set.data(seriesData)
            val series1Mapping = set.mapAs("{ x: 'x', value: 'value' }")

            val series1 = cartesian.column(series1Mapping)
            series1.name("Student Progress")
            series1.hovered().markers().enabled(true)
            series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4.0)
            series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5.0)
                .offsetY(5.0)




            anyChartView.setChart(cartesian)

        }

        override fun onCancelled(databaseError: DatabaseError) {
            Toast.makeText(p1.context, databaseError.toException().toString(), Toast.LENGTH_SHORT)
        }
    })
}




internal class StudentProgressDataEntryInTeacher internal constructor(
    x: String?,
    value: Number?,

) :
    ValueDataEntry(x, value) {

}

