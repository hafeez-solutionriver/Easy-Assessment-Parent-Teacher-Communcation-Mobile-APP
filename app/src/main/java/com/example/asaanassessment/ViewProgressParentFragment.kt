package com.example.asaanassessment



import android.content.Context
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
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipPositionMode
import com.anychart.graphics.vector.Stroke


class ViewProgressParentFragment() : Fragment() {

    var StudentIndexSelected: Int = -1
    var SubjectIndexSelected: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_progress_parent, container, false)


        val autoCompleteTextViewSubject: AutoCompleteTextView =
            view.findViewById(R.id.parent_subject_selection)
        val items = listOf("English", "Maths", "Physics")

// Create an ArrayAdapter with the items and set it to the AutoCompleteTextView
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        autoCompleteTextViewSubject.setAdapter(adapter)

// Add an item click listener to the AutoCompleteTextView
        autoCompleteTextViewSubject.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as String


            SubjectIndexSelected = position

            if (StudentIndexSelected != -1 && SubjectIndexSelected != -1) {

                displayGraph(requireView())
            }

        }


            val autoCompleteTextViewStudent: AutoCompleteTextView =
                view.findViewById(R.id.parent_student_selection)
            val student = listOf("Hafeez", "Rauf", "Qudoos")

// Create an ArrayAdapter with the items and set it to the AutoCompleteTextView
            val adapterStudent =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, student)
            autoCompleteTextViewStudent.setAdapter(adapterStudent)

// Add an item click listener to the AutoCompleteTextView
            autoCompleteTextViewStudent.setOnItemClickListener { parent, view, position, id ->

                StudentIndexSelected = position

                if (StudentIndexSelected != -1 && SubjectIndexSelected != -1) {

    displayGraph(requireView())
                }

            }






                return view
                //
            }
        }





    fun displayGraph(p1: View?)
    {

        val anyChartView: AnyChartView = p1!!.findViewById<AnyChartView>(R.id.line_chart_parent)


        val cartesian = AnyChart.line()

        cartesian.animation(true)

        cartesian.padding(10.0, 20.0, 5.0, 20.0)

        cartesian.crosshair().enabled(true)
        cartesian.crosshair()
            .yLabel(true) //
            .yStroke(null as Stroke?, null, null, null as String?, null as String?)

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

        cartesian.title("Trend of Sales of the Most Popular Products of ACME Corp.")

        cartesian.yAxis(0).title("Number of Bottles Sold (thousands)")
        cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)

        val seriesData: MutableList<DataEntry> = ArrayList()
        seriesData.add(CustomDataEntry("1986", 3.6, 2.3, 2.8))
        seriesData.add(CustomDataEntry("1987", 7.1, 4.0, 4.1))


        val set = Set.instantiate()
        set.data(seriesData)
        val series1Mapping = set.mapAs("{ x: 'x', value: 'value' }")


        val series1 = cartesian.line(series1Mapping)
        series1.name("Brandy")
        series1.hovered().markers().enabled(true)
        series1.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
        series1.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)





        cartesian.legend().enabled(true)
        cartesian.legend().fontSize(13.0)
        cartesian.legend().padding(0.0, 0.0, 10.0, 0.0)

        anyChartView.setChart(cartesian)
    }



    private class CustomDataEntry internal constructor(
        x: String?,
        value: Number?,
        value2: Number?,
        value3: Number?
    ) :
        ValueDataEntry(x, value) {
        init {
            setValue("value2", value2)
            setValue("value3", value3)
        }
    }

