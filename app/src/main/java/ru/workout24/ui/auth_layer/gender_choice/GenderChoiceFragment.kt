package ru.workout24.ui.auth_layer.gender_choice


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View

import ru.workout24.R
import ru.workout24.ui.auth_layer.anket_layer.AnketViewModel
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_gender_choice.*
import java.util.*
import androidx.lifecycle.Observer
import ru.workout24.utills.custom_views.CustomButton
import org.jetbrains.anko.find


/**
 * Фрагмент для выбора пола в первоначальной анкете
 *
 */
class GenderChoiceFragment : BaseFragment(R.layout.fragment_gender_choice), View.OnClickListener {

    private val viewModel: AnketViewModel by lazy {
        attachViewModel<AnketViewModel>(
            AnketViewModel::class.java, false
        )
        { code, message ->
            when (code) {
                AnketViewModel.SEND_MAIN_PARAMS -> {
                    pref.gender_date_selected = true
                    controller.navigate(R.id.action_genderChoiceFragment_to_sub_layer)
                    view?.find<CustomButton>(R.id.btn_next)?.showProgress(false)
                }
                else -> {
                    showInfoAlert(context!!, "Ошибка", message, "OK", {}).show()
                    view?.find<CustomButton>(R.id.btn_next)?.showProgress(false)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_next -> {
                btn_next.showProgress(true)
                viewModel.sendInfo(AnketViewModel.SEND_MAIN_PARAMS)
            }
            R.id.female_view -> {
                female_view.background = resources.getDrawable(R.drawable.gender_light_gradient)
                male_view.background = resources.getDrawable(R.drawable.gender_dark_gradient)
                viewModel.gender.value = "FEMALE"
            }
            R.id.male_view -> {
                female_view.background = resources.getDrawable(R.drawable.gender_dark_gradient)
                male_view.background = resources.getDrawable(R.drawable.gender_light_gradient)
                viewModel.gender.value = "MALE"
            }
            R.id.text_birthday -> {
                val c = Calendar.getInstance()

                val dpd = DatePickerDialog(
                    activity, AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        c.set(Calendar.YEAR, year)
                        c.set(Calendar.MONTH, monthOfYear)
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        viewModel.birthdayRowValue.value = c
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
                )
                dpd.datePicker.maxDate = System.currentTimeMillis()

                dpd.show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout.hideBackButton()
        btn_next.setOnClickListener(this)
        female_view.setOnClickListener(this)
        male_view.setOnClickListener(this)
        text_birthday.setOnClickListener(this)
        viewModel.birthdayUI.observe(this, Observer {
            it?.let { birthday ->
                text_birthday.text = birthday
                text_birthday.setTextColor(resources.getColor(R.color.white))
                viewModel.gender.value?.let {
                    btn_next.isEnable = true
                }
            }
        })
        viewModel.gender.observe(this, Observer {
            it?.let { gender ->
                if (gender == "MALE") {
                    female_view.background = resources.getDrawable(R.drawable.gender_dark_gradient)
                    male_view.background = resources.getDrawable(R.drawable.gender_light_gradient)
                } else {
                    male_view.background = resources.getDrawable(R.drawable.gender_dark_gradient)
                    female_view.background = resources.getDrawable(R.drawable.gender_light_gradient)
                }
                viewModel.birthdayUI.value?.let {
                    btn_next.isEnable = true
                }
            }
        })
//        btn_next.showProgress(true)
    }
}
