package ru.workout24.utills


import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import ru.workout24.R
import ru.workout24.utills.base.BaseFragment
import com.collapsiblecalendarview.widget.CollapsibleCalendar
import com.collapsiblecalendarview.widget.UICalendar
import kotlinx.android.synthetic.main.fragment_playground.*




class PlaygroundFragment : BaseFragment(R.layout.fragment_playground) {
    var factor: Float? = null
    var b:Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val circlesCount = 5
        val current = 2
        initLayout(circlesCount, current)

        calendarView.expand(0);

        calendarView.setExpandIconVisible(false);
        calendarView.setButtonLeftVisibility(View.GONE);
        calendarView.setButtonRightVisibility(View.GONE);
        calendarView.setMonthAndYearVisibility(View.GONE);
        calendarView.setFirstDayOfWeek(UICalendar.MONDAY);
        //Осторожно, месяц на 1 меньше, остальное нормальное
        calendarView.addEventTag(2019,7,21, resources.getColor(R.color.tomato),Color.WHITE)
        calendarView.addEventTag(2019,7,1, resources.getColor(R.color.tomato),Color.WHITE)
        calendarView.setCalendarListener(object : CollapsibleCalendar.CalendarListener {
            override fun onDaySelect() {

            }

            override fun onItemClick(v: View) {

            }

            override fun onDataUpdate() {

            }

            override fun onMonthChange() {

            }

            override fun onWeekChange(position: Int) {

            }
        })

        linear_layout.setOnClickListener {
            b = !b
            if (b)
                calendarView.expand(100)
            else
                calendarView.collapse(100)
        }

    }

    fun initLayout(count: Int, current: Int) {

        if (count <= 0 || current <= 0)
            throw RuntimeException("Сount and current should be >0!")
        if (current>count)
            throw RuntimeException("Current should be <= then count")


        linear_layout.removeAllViews()
        linear_layout.orientation = LinearLayout.HORIZONTAL
        //для того, чтобы размеры были в DP
        factor = context?.resources?.displayMetrics?.density
        //первый круг всегда есть и он всегда красный
        val iv1 = ImageView(context)
        iv1.setImageResource(R.drawable.red_circle)
        iv1.layoutParams = ViewGroup.LayoutParams(16 * factor?.toInt()!!, 16 * factor!!.toInt())

        linear_layout.addView(iv1)


        //рисуем по количеству заданных кругов
        for (i in 1 until count) {
            val iv2 = ImageView(context)
            //если не дошли до текущего, то красное, иначе белое
            if (i < current)
                iv2.setImageResource(R.drawable.red_line)
            else if (i == current)

                iv2.setImageResource(R.drawable.red_white_gradient_line)
            else
                iv2.setImageResource(R.drawable.white_line)


            iv2.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )

            linear_layout.addView(iv2)
            val iv = ImageView(context)
            //если не дошли до текущего, то красное, иначе белое
            if (i < current)
                iv.setImageResource(R.drawable.red_circle)
            else
                iv.setImageResource(R.drawable.white_circle_progress)
            iv.layoutParams = ViewGroup.LayoutParams(16 * factor?.toInt()!!, 16 * factor!!.toInt())

            linear_layout.addView(iv)


        }

    }

}
