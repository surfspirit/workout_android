package ru.workout24.ui.auth_layer.anket_layer.final_part


import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer

import ru.workout24.R
import ru.workout24.ui.auth_layer.anket_layer.AnketViewModel
import ru.workout24.utills.base.BaseFragment
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_weight_height.*

class WeightHeightFragment : BaseFragment(R.layout.fragment_weight_height), View.OnClickListener {

    private val viewModel: AnketViewModel by lazy {
        attachViewModel<AnketViewModel>(
            AnketViewModel::class.java, false
        )
        { code, message ->

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_next -> {
                viewModel.weight.value?.let {
                    if (it < 40 || it > 200) {
                        showInfoAlert(context!!, "Ошибка", "Некорректное значение веса", "OK", {}).show()
                        text_weight_value.setTextColor(resources.getColor(R.color.red_light))
                        return
                    }
                }
                viewModel.height.value?.let {
                    if (it < 100 || it > 250) {
                        showInfoAlert(context!!, "Ошибка", "Некорректное значение роста", "OK", {}).show()
                        text_height_value.setTextColor(resources.getColor(R.color.red_light))
                        return
                    }
                }
                viewModel.percent.value?.let {
                    if (it < 0 || it > 60) {
                        showInfoAlert(context!!, "Ошибка", "Некорректное значение процента жира", "OK", {}).show()
                        text_percent_value.setTextColor(resources.getColor(R.color.red_light))
                        return
                    }
                }
                controller.navigate(R.id.action_weightHeightFragment_to_placeholder)
            }
            R.id.layout_weight -> {
                //text_weight_value
                showCreateCategoryDialog(R.id.layout_weight, viewModel.weight.value?.toString())
            }
            R.id.layout_height -> {
                //text_height_value
                showCreateCategoryDialog(R.id.layout_height, viewModel.height.value?.toString())
            }
            R.id.layout_percent -> {
                //text_percent_value
                showCreateCategoryDialog(R.id.layout_percent, viewModel.percent.value?.toString())
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout.hideBackButton()
        viewModel.changes.observe(this, Observer {
            it.weight?.let {
                text_weight_value.text = "$it кг"
                text_weight_value.setTextColor(resources.getColor(R.color.white))
            }
            it.height?.let {
                text_height_value.text = "$it см"
                text_height_value.setTextColor(resources.getColor(R.color.white))
            }
            it.percent?.let {
                text_percent_value.text = "$it %"
                text_percent_value.setTextColor(resources.getColor(R.color.white))
            }
            btn_next.isEnabled = viewModel.isAllParamsFill()
        })

        btn_next.setOnClickListener(this)
        layout_height.setOnClickListener(this)
        layout_percent.setOnClickListener(this)
        layout_weight.setOnClickListener(this)
    }

    private fun showCreateCategoryDialog(id: Int, value: String?) {
        val builder = AlertDialog.Builder(this.requireContext())

        val view = layoutInflater.inflate(R.layout.dialog_with_input, null)
        val categoryEditText = view.findViewById(R.id.text_input) as EditText
        val titleEditText = view.findViewById(R.id.text_dialog_title) as TextView
        val layoutEditText = view.findViewById(R.id.til_text_input) as TextInputLayout
        if (value!=null)
            categoryEditText.setText(value)

        when (id) {
            R.id.layout_weight -> {
                titleEditText.text = "Введите значение веса"
                layoutEditText.hint = "Вес, кг"
            }
            R.id.layout_height -> {
                titleEditText.text = "Введите значение роста"
                layoutEditText.hint = "Рост, см"
            }
            R.id.layout_percent -> {
                titleEditText.text = "Введите значение процента жира"
                layoutEditText.hint = "Процент жира"
            }
        }

        builder.setView(view)
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            val input = categoryEditText.text
            var isValid = true
            if (input?.isBlank()!!) {
                layoutEditText.error = "Введено некорректное значение"
                isValid = false
            }

            if (isValid) {
                when (id) {
                    R.id.layout_weight -> {
                        viewModel.weight.value = input.toString().toFloat()
//                        if (input.toString().toFloat() < 40.0) {
//                            text_weight_value.setTextColor(resources.getColor(R.color.red_light))
//                        }
//                        else {
//                            text_weight_value.setTextColor(resources.getColor(R.color.white))
//                        }
                    }
                    R.id.layout_height -> {
                        viewModel.height.value = input.toString().toFloat()
                    }
                    R.id.layout_percent -> {
                        viewModel.percent.value = input.toString().toFloat()
                    }
                }
            }

            if (isValid) {
                dialog.dismiss()
            }
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }

        builder.show()
    }
}
