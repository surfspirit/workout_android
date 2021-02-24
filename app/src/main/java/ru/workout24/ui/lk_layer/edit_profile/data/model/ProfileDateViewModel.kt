package ru.workout24.ui.lk_layer.edit_profile.data.model

import ru.workout24.utills.ITEM_TYPE
import ru.workout24.utills.dateFrom_dd_mm_yyyy
import java.text.SimpleDateFormat
import java.util.*

class ProfileDateViewModel(
    title: String,
    val date: String,
    id: String? = null,
    val canModify: Boolean = true,
    val withTime: Boolean = false
) :
    ProfileTextViewModel(ITEM_TYPE.DATE, title, date, null, id) {

    fun valueToDate(): Date = date.dateFrom_dd_mm_yyyy() ?: Date()

    fun dateToString(date: Date): String = SimpleDateFormat("dd.MM.yyyy").format(date)
}