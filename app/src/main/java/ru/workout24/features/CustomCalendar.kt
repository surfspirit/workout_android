package ru.workout24.features

import android.util.Log
import java.util.*

class CustomCalendar {
    private val calendar = Calendar.getInstance().apply {
        // начинаем отсчитывать с понедельника
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    }
    private val todayMonth = calendar.get(Calendar.MONTH)
    private val todayWeekIndex = calendar.get(Calendar.WEEK_OF_MONTH)
    private val currentWeekIndex get() = calendar.get(Calendar.WEEK_OF_MONTH)

    fun nextWeek() {
        calendar.add(Calendar.WEEK_OF_MONTH, 1)
        log()
    }

    fun prevWeek() {
        calendar.add(Calendar.WEEK_OF_MONTH, -1)
        log()
    }

    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
        log()
    }

    fun prevMonth() {
        calendar.add(Calendar.MONTH, -1)
        log()
    }

    fun discardWeekIndex() {
        if (calendar.get(Calendar.MONTH) == todayMonth) {
            calendar.set(Calendar.WEEK_OF_MONTH, todayWeekIndex)
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1)
        }
    }

    /**
     * Получаем дни недели [WeekDay] по позиции текцщей недели
     */
    fun getCurrentWeekDays(): List<WeekDay> = mutableListOf<WeekDay>().apply {
        val tempCalendar = calendar.clone() as Calendar
        for (index in 0..6){
            add(
                WeekDay(
                    tempCalendar.get(Calendar.DAY_OF_MONTH),
                    tempCalendar.get(Calendar.MONTH)
                )
            )
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    fun asCalendar() = calendar

    private fun log() {
        Log.i(this::class.simpleName,
            "Month-${calendar.get(Calendar.MONTH)} \n" +
                    "CurrentWeek-${currentWeekIndex} \n" +
                    "Week[${getCurrentWeekDays()}]")
    }

    data class WeekDay(
        val day: Int,
        val month: Int
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as WeekDay

            if (day != other.day) return false
            if (month != other.month) return false

            return true
        }

        override fun hashCode(): Int {
            var result = day
            result = 31 * result + month
            return result
        }
    }
}