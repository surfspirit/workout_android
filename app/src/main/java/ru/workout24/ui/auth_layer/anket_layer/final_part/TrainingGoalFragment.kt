package ru.workout24.ui.auth_layer.anket_layer.final_part


import android.os.Bundle
import android.util.Log
import android.view.View

import ru.workout24.R
import ru.workout24.ui.auth_layer.anket_layer.AnketViewModel
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.custom_views.CustomButton
import ru.workout24.utills.custom_views.RadioGroupConstraintLayout
import kotlinx.android.synthetic.main.fragment_training_goal.*
import org.jetbrains.anko.find

class TrainingGoalFragment : BaseFragment(R.layout.fragment_training_goal), View.OnClickListener {

    private val viewModel: AnketViewModel by lazy {
        attachViewModel<AnketViewModel>(
            AnketViewModel::class.java, false
        )
        { code, message ->
            when (code) {
                AnketViewModel.SEND_SUB_PARAMS -> {
                    pref.anket_passed = true
                    controller.navigate(R.id.action_global_test_layer)
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
                viewModel.sendInfo(AnketViewModel.SEND_SUB_PARAMS)
            }
            R.id.c_first -> {
                radio_group.check(R.id.rb_first)
            }
            R.id.c_second -> {
                radio_group.check(R.id.rb_second)
            }
            R.id.c_third -> {
                radio_group.check(R.id.rb_third)
            }
            R.id.view13 -> {
                radio_group_trainings_count.checkMe(R.id.radioButton1)
            }
            R.id.view14 -> {
                radio_group_trainings_count.checkMe(R.id.radioButton2)
            }
            R.id.view16 -> {
                radio_group_trainings_count.checkMe(R.id.radioButton3)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appBarLayout.setOnBackClick {
            controller.popBackStack()
        }
        btn_next.setOnClickListener(this)
        radio_group.setOnCheckedChangeListener { group, checkedId -> setCheckedGoal(group, checkedId) }
        radio_group_trainings_count.setRadioBtnsChangeListener { group, checkedId -> setCheckedCount(group, checkedId) }
        c_first.setOnClickListener(this)
        c_second.setOnClickListener(this)
        c_third.setOnClickListener(this)
        view13.setOnClickListener(this)
        view14.setOnClickListener(this)
        view16.setOnClickListener(this)

        viewModel.trainingGoal.value?.let {
            radio_group.check(it)
        }
        viewModel.trainingCount.value?.let {
            radio_group_trainings_count.checkMe(it)
            viewModel.trainingGoal.value?.let {
                btn_next.isEnabled = true
            }
        }
    }

    private fun setCheckedGoal(group: RadioGroupConstraintLayout, checkedId: Int) {
        //btn_next.isEnabled = true
        when (checkedId) {
            R.id.rb_first -> {
                Log.d("setChecked", "radio_button_goal_first")
            }
            R.id.rb_second -> {
                Log.d("setChecked", "radio_button_goal_second")
            }
            R.id.rb_third -> {
                Log.d("setChecked", "radio_button_goal_third")
            }
        }
        viewModel.trainingGoal.value = checkedId
        viewModel.trainingCount.value?.let {
            btn_next.isEnable = true
        }
    }

    private fun setCheckedCount(group: RadioGroupConstraintLayout, checkedId: Int) {
        //btn_next.isEnable = true
        when (checkedId) {
            R.id.radioButton1 -> {
                Log.d("setChecked", "radioButton1")
            }
            R.id.radioButton2 -> {
                Log.d("setChecked", "radioButton2")
            }
            R.id.radioButton3 -> {
                Log.d("setChecked", "radioButton3")
            }
        }
        viewModel.trainingCount.value = checkedId
        viewModel.trainingGoal.value?.let {
            btn_next.isEnable = true
        }
    }
}
