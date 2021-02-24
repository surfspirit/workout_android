package ru.workout24.ui.lk_layer.edit_profile.utils

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import com.aigestudio.wheelpicker.WheelPicker
import ru.workout24.R
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileDateViewModel
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileRangeViewModel
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileSelectEnumViewModel
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileTextViewModel
import com.google.android.material.textfield.TextInputLayout
import java.util.*

object Dialogs {
    fun showTextInputDialog(
        context: Context,
        item: ProfileTextViewModel,
        positiveCallback: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_with_input, null)
        val categoryEditText = view.findViewById(R.id.text_input) as EditText
        val titleEditText = view.findViewById(R.id.text_dialog_title) as TextView
        val layoutEditText = view.findViewById(R.id.til_text_input) as TextInputLayout
        titleEditText.text = item.hint
        if (item is ProfileRangeViewModel) {
            layoutEditText.hint = item.title + ", " + item.suffix
            categoryEditText.setText(item.value)
        } else {
            //В layout диалога по умолчанию указаны только числа
            categoryEditText.inputType = InputType.TYPE_CLASS_TEXT
            layoutEditText.hint = item.title
            categoryEditText.setText(item.value)
        }
        builder.setView(view)
        builder.setPositiveButton(android.R.string.ok, null)
        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val input = categoryEditText.text
                if (input?.isEmpty()!! && item.placeHolder == null) {
                    layoutEditText.error = "Введено некорректное значение"
                    return@setOnClickListener
                }
                if (item is ProfileRangeViewModel) {
                    val value = input.toString().toInt()
                    if (value < item.min) {
                        layoutEditText.error = "Минимальное значение: ${item.min}"
                        return@setOnClickListener
                    }
                    if (value > item.max) {
                        layoutEditText.error = "Максимальное значение: ${item.max}"
                        return@setOnClickListener
                    }
                }
                item.value = input.toString()
                positiveCallback()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun showWheelInputDialog(
        context: Context,
        item: ProfileSelectEnumViewModel<*>,
        positiveCallback: () -> Unit
    ) {
        val builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_with_select, null)
        val wheelPicker = view.findViewById(R.id.wheelPicker) as WheelPicker
        val titleEditText = view.findViewById(R.id.text_dialog_title) as TextView
        titleEditText.text = item.hint
        wheelPicker.data = item.keyMap.values.toMutableList()
        wheelPicker.selectedItemPosition = item.keyMap.values.indexOf(item.value)
        builder.setView(view)
        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            if (item.variants.isNotEmpty() && item.keyMap.isNotEmpty()) {
                val key = item.variants[wheelPicker.currentItemPosition]
                val newValue = item.keyMap[key]!!
                if (item.value != newValue) {
                    item.value = newValue
                    positiveCallback()
                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 -> dialog.cancel() }
        builder.show()
    }

    fun showDatePickerDialog(
        context: Context,
        item: ProfileDateViewModel,
        maxDate: Long = System.currentTimeMillis(),
        minDate: Long? = null,
        positiveCallback: (Calendar) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        calendar.time = item.valueToDate()
        val dpd = DatePickerDialog(
            context,
            AlertDialog.THEME_DEVICE_DEFAULT_DARK,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                item.value = item.dateToString(calendar.time)
                positiveCallback(calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dpd.datePicker.maxDate = maxDate
        if (minDate != null) dpd.datePicker.minDate = minDate
        dpd.show()
    }
}