package com.example.asaanassessment



import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.data.Set
import com.anychart.enums.Anchor
import com.anychart.enums.LabelsOverlapMode
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipPositionMode
import com.anychart.graphics.vector.Stroke
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ViewProgressParentFragment(var parentId:String) : Fragment() {

    companion object{
        var isAlreadyShownGraph =false
    }
    var StudentIndexSelected: Int = -1
    var SubjectIndexSelected: Int = -1

    private lateinit var mContext: Context

    lateinit var subjectIds: MutableList<String>
    lateinit var subjectNames: MutableList<String>


    lateinit var studentIds: MutableList<String>
    lateinit var studentNames: MutableList<String>

    lateinit var studentSubjects: MutableMap<String, MutableList<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_view_progress_parent, container, false)





        val autoCompleteTextViewStudent: AutoCompleteTextView =
                view.findViewById(R.id.parent_student_selection)
        val autoCompleteTextViewSubject: AutoCompleteTextView =
            view.findViewById(R.id.parent_subject_selection)


        val studentRef = FirebaseDatabase.getInstance().getReference("Student")



        studentRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {


                studentNames = mutableListOf<String>()
                studentIds = mutableListOf<String>()
                studentSubjects = hashMapOf()

                for (studentSnapshot in dataSnapshot.children) {

                    if (studentSnapshot.child("ParentId").getValue(String::class.java)?.equals(parentId)!!)
                    {
                        val studentFirstName =
                            studentSnapshot.child("FirstName")
                                .getValue(String::class.java)

                        val studentLastName = studentSnapshot.child("LastName")
                            .getValue(String::class.java)

                        studentNames.add(studentFirstName.toString()+" "+studentLastName)
                        studentIds.add(studentSnapshot.key.toString())


                        var subjects  = mutableListOf<String>()
                        for(subject in studentSnapshot.child("Subjects").children)
                        {
                            subjects.add(subject.value.toString())
                        }

                        studentSubjects[studentSnapshot.key.toString()] = subjects
                    }


                }

                val items = mutableListOf<String>()

                for (index in studentNames.indices) {
                    items.add(studentNames[index] + "  -  " + studentIds[index])
                }


                val adapterStudent =
                    ArrayAdapter(mContext, android.R.layout.simple_dropdown_item_1line, items)
                autoCompleteTextViewStudent.setAdapter(adapterStudent)





// Create an ArrayAdapter with the items and set it to the AutoCompleteTextView

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
        autoCompleteTextViewStudent.setOnItemClickListener { parent, view, position, id ->

            StudentIndexSelected = position

            val subjectsRef = FirebaseDatabase.getInstance().getReference("Subject")

            subjectsRef.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {


                    subjectNames = mutableListOf<String>()
                    subjectIds = mutableListOf<String>()


                    for(subject in dataSnapshot.children)
                    {
                        if(studentSubjects[studentIds[StudentIndexSelected]]?.contains(subject.key.toString())!!)
                        {
                            subjectNames.add(subject.child("CourseName").getValue(String::class.java).toString())
                            subjectIds.add(subject.key.toString())
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


        }



        autoCompleteTextViewSubject.setOnItemClickListener { parent, view, position, id ->



            SubjectIndexSelected = position

            if (StudentIndexSelected != -1 && SubjectIndexSelected != -1) {


                        getHomeWorkData(requireView(),subjectIds[SubjectIndexSelected],studentIds[StudentIndexSelected],subjectNames[SubjectIndexSelected])

            }

        }


                return view
                //
            }
        }



fun getHomeWorkData(parentView: View,subjectId: String, studentId: String,subjectName:String):MutableList<DataEntry>{

    val seriesData : MutableList<DataEntry> = ArrayList()

    val database = FirebaseDatabase.getInstance()
    val homeworkRef = database.getReference("Homework")

    homeworkRef.addListenerForSingleValueEvent(object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {





            for (homeworkSnapshot in dataSnapshot.children) {

                val databaseSubjectId =
                    homeworkSnapshot.child("subjectId").getValue(String::class.java).toString()
                val databaseStudentId =
                    homeworkSnapshot.child("studentId").getValue(String::class.java).toString()
                val databaseRating =
                    homeworkSnapshot.child("rating").getValue(Long::class.java).toString()

                if (databaseSubjectId.equals(subjectId) && databaseStudentId.equals(studentId)) {


                    val assignmentName =
                        homeworkSnapshot.child("assignmentName").getValue(String::class.java)
                            .toString()

                    if (assignmentName != null) {
                        seriesData.add(
                            StudentProgressDataEntryInParent(
                                assignmentName,
                                databaseRating.toDouble()
                            )
                        )



                    }
                }
            }

            displayGraphParent(parentView,seriesData, subjectName,parentView.context)
        }



        override fun onCancelled(databaseError: DatabaseError) {

        }
    })



return seriesData

}

fun displayGraphParent(p1: View?, seriesData: MutableList<DataEntry>,subjectName:String,mContext:Context) {


    val anyChartView: AnyChartView = p1!!.findViewById(R.id.line_chart_parent)

    if(seriesData.size==0)
    {
       if(ViewProgressParentFragment.isAlreadyShownGraph){
           anyChartView.clear()
       }

        Toast.makeText(mContext,"No Record found for given subject homework!",Toast.LENGTH_SHORT).show()
    }

    else
    {
        ViewProgressParentFragment.isAlreadyShownGraph=true
        val cartesian = AnyChart.column()


        cartesian.animation(true)

        cartesian.padding(10.0, 10.0, 30.0, 20.0)
        anyChartView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        // Enable scrolling
        cartesian.xScroller().enabled(true)
        cartesian.xZoom().setToPointsCount(7, false, null)

        // Allow overlapping labels
        cartesian.xAxis(0).overlapMode(LabelsOverlapMode.ALLOW_OVERLAP)

        cartesian.crosshair().enabled(true)
        cartesian.crosshair()
            .yLabel(true)
            .yStroke(null as Stroke?, null, null, null as String?, null as String?)

        cartesian.tooltip().positionMode(TooltipPositionMode.CHART)

        cartesian.title("Student Progress in : $subjectName")

        cartesian.yAxis(0).title("Progress out of 5")
        cartesian.yScale().minimum(0.0)
        cartesian.yScale().maximum(5.0)
        cartesian.yScale().ticks().interval(0.5)

        cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)
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

}

internal class StudentProgressDataEntryInParent internal constructor(
    x: String?,
    value: Number?,

    ) :
    ValueDataEntry(x, value) {

}