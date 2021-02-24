package ru.workout24.utills

import android.app.DatePickerDialog
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.app.AlertDialog
import androidx.navigation.NavController
import com.aigestudio.wheelpicker.WheelPicker
import ru.workout24.R
import ru.workout24.utills.base.BaseAdapter
import com.google.android.material.textfield.TextInputLayout
import com.suke.widget.SwitchButton
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SettingsRecyclerAdapter(
    val context: Context,
    val layoutInflater: LayoutInflater,
    val controller: NavController
) : BaseAdapter(R.layout.item_profile_edit) {
    init {


        onBind { holder, pos ->
            val list = data as ArrayList<Any>
            val item = (data[pos]) as SettingsRecyclerItem

            holder.find<View>(R.id.i_profile_type_1).visible(false)
            holder.find<View>(R.id.i_profile_type_2).visible(false)
            holder.find<View>(R.id.i_profile_type_3).visible(false)
            holder.find<View>(R.id.i_profile_type_4).visible(false)
            holder.find<View>(R.id.i_profile_type_5).visible(false)
            holder.find<View>(R.id.field_value_t1_2).hide()

            when (item.type) {
                ITEM_TYPE.SWITCH -> {
                    holder.find<View>(R.id.i_profile_type_2).visible(true)
                    val name = holder.find<TextView>(R.id.field_name_t2)
                    val value = holder.find<SwitchButton>(R.id.field_value_t2)
                    name.text = item.name
                    value.isChecked = item.value as Boolean
                    holder.find<View>(R.id.i_profile_type_2).setOnClickListener {
                        value.toggle()
                    }
                    value.setOnCheckedChangeListener { view, isChecked ->
                        item.value = isChecked
                    }

                }
                ITEM_TYPE.SPACER -> {
                    holder.find<View>(R.id.i_profile_type_3).visible(true)
                }
                ITEM_TYPE.ARROW -> {
                    val h = holder.find<View>(R.id.i_profile_type_4)
                    h.visible(true)
                    val name = holder.find<TextView>(R.id.field_name_t4)
                    name.text = item.name
                }
                ITEM_TYPE.WHITE_SPACER -> holder.find<View>(R.id.i_profile_type_5).visible(true)
                else -> {
                    holder.find<View>(R.id.i_profile_type_1).visible(true)
                    val name = holder.find<TextView>(R.id.field_name_t1)
                    val value = holder.find<TextView>(R.id.field_value_t1)
                    name.text = item.name
                    when (item.type) {
                        ITEM_TYPE.TEXT -> value.text = item.value as String
                        ITEM_TYPE.DATE -> value.text =
                            SimpleDateFormat("dd.MM.yyyy").format(item.value)
                        ITEM_TYPE.DATE_TIME -> {
                            val time = holder.find<TextView>(R.id.field_value_t1_2)
                            time.show()
                            value.text =
                                SimpleDateFormat("dd.MM.yyyy").format(item.value)
                            time.text =
                                SimpleDateFormat("HH:mm").format(item.value)
                        }
                        ITEM_TYPE.SELECT -> when (item.value) {
                            is GENDER -> value.text = genders[item.value as GENDER]
                            is TRAINING_LEVEL -> value.text =
                                levels[(item.value as TRAINING_LEVEL).value]
                            is GOAL -> value.text = shortGoals[(item.value as GOAL).value]
                        }
                        ITEM_TYPE.NUMBER -> if (item.placeholder.isBlank()) {
                            val itemValue = item.value
                            val number = when {
                                itemValue is Double -> itemValue.toInt()
                                itemValue is Float -> itemValue.toInt()
                                else -> item.value
                            }
                            value.text = number.toString() + " " + item.suffix
                        } else {
                            value.text = item.placeholder
                        }
                        else -> {
                            holder.find<View>(R.id.i_profile_type_1).visible(false)
                            holder.find<View>(R.id.i_profile_type_2).visible(false)
                            holder.find<View>(R.id.i_profile_type_3).visible(false)
                            holder.find<View>(R.id.i_profile_type_4).visible(false)
                            holder.find<View>(R.id.i_profile_type_5).visible(false)
                            holder.find<View>(R.id.field_value_t1_2).hide()
                        }
                    }
                }
            }

            setOnItemClick { pos ->
                val item = (data[pos]) as SettingsRecyclerItem
                if (!item.lock) {
                    when (item.type) {
                        ITEM_TYPE.ARROW -> controller.navigate(item.value as Int)
                        ITEM_TYPE.TEXT, ITEM_TYPE.NUMBER -> showTextInputDialog(item)
                        ITEM_TYPE.SELECT -> showWheelInputDialog(item)
                        ITEM_TYPE.DATE -> {
                            val c = Calendar.getInstance()
                            val i = item.value as Date
                            c.set(Calendar.YEAR, i.year + 1900)
                            c.set(Calendar.MONTH, i.month)
                            c.set(Calendar.DAY_OF_MONTH, i.date)

                            val dpd = DatePickerDialog(
                                context,
                                AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                                    c.set(Calendar.YEAR, year)
                                    c.set(Calendar.MONTH, monthOfYear)
                                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                    item.value = Date(
                                        c.get(Calendar.YEAR) - 1900,
                                        c.get(Calendar.MONTH),
                                        c.get(Calendar.DAY_OF_MONTH)
                                    )
                                    notifyDataSetChanged()
                                },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                            )
                            dpd.datePicker.maxDate = System.currentTimeMillis()

                            dpd.show()
                        }
                    }
                }
            }


        }


    }

    //Предполагается что сюда будут попадать только TEXT и NUMBER
    private fun showTextInputDialog(item: SettingsRecyclerItem) {
        val builder = AlertDialog.Builder(context)

        val view = layoutInflater.inflate(R.layout.dialog_with_input, null)
        val categoryEditText = view.findViewById(R.id.text_input) as EditText
        val titleEditText = view.findViewById(R.id.text_dialog_title) as TextView
        val layoutEditText = view.findViewById(R.id.til_text_input) as TextInputLayout
        titleEditText.text = item.question
        if (item.type == ITEM_TYPE.TEXT) {
            categoryEditText.inputType =
                InputType.TYPE_CLASS_TEXT//В layout диалога по умолчанию указаны только числа
            layoutEditText.hint = item.name
            categoryEditText.setText(item.value as String)
        } else {
            layoutEditText.hint = item.name + ", " + item.suffix
            categoryEditText.setText(item.value.toString())
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
                var isValid = true
                if (input?.isEmpty()!!) {
                    layoutEditText.error = "Введено некорректное значение"
                    isValid = false
                }

                if (isValid) {
                    item.placeholder = ""
                    if (item.type == ITEM_TYPE.TEXT)
                        item.value = input.toString()
                    else
                        item.value = input.toString().toFloat()
                    notifyDataSetChanged()
                }

                if (isValid) {
                    dialog.dismiss()
                }
            }

        }
        dialog.show()


    }

    private fun showWheelInputDialog(item: SettingsRecyclerItem) {
        val builder = AlertDialog.Builder(context)

        val view = layoutInflater.inflate(R.layout.dialog_with_select, null)
        val wheelPicker = view.findViewById(R.id.wheelPicker) as WheelPicker
        val titleEditText = view.findViewById(R.id.text_dialog_title) as TextView
        titleEditText.text = item.question

        val list = arrayListOf<String>()
        for (it in item.variants) {
            when (it) {
                is GOAL -> shortGoals[it.value]?.let { list.add(it) }
                is TRAINING_LEVEL -> levels[it.value]?.let { list.add(it) }
                is GENDER -> genders[it]?.let { list.add(it) }
            }
        }
        wheelPicker.data = list
        wheelPicker.selectedItemPosition = item.variants.indexOf(item.value)

        builder.setView(view)
        builder.setPositiveButton(android.R.string.ok, { dialog, which ->
            item.value = item.variants[wheelPicker.currentItemPosition]
            notifyDataSetChanged()
            dialog.dismiss()
        })

        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }

        builder.show()


    }

}


data class SettingsRecyclerItem(
    val type: ITEM_TYPE = ITEM_TYPE.SPACER,
    var name: String = "",
    var value: Any = "",//В зависимости от типа либо текущее значение, либо для типа ARROW - то куда переходить
    var min: Any = 0,
    var max: Any = 0,
    var suffix: String = "",
    var variants: ArrayList<Any> = arrayListOf(),
    var question: String = "",
    var placeholder: String = "",
    var lock: Boolean = false
)

enum class ITEM_TYPE(val value: Int) {
    TEXT(0),
    DATE(1),
    NUMBER(2),
    SELECT(3),
    SWITCH(4),
    SPACER(5),
    ARROW(6),
    WHITE_SPACER(7),
    DATE_TIME(8)
}
