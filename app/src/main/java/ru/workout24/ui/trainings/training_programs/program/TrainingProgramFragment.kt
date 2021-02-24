package ru.workout24.ui.trainings.training_programs.program


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import ru.workout24.R
import ru.workout24.ui.trainings.training_programs.pojos.Training
import ru.workout24.ui.trainings.training_programs.pojos.TrainingProgram
import ru.workout24.utills.*
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_training_program.*
import org.jetbrains.anko.find


class TrainingProgramFragment : BaseFragment(R.layout.fragment_training_program) {
    private val onlyShow get() = arguments?.getBoolean("onlyShow", false) ?: false

    private val adapter: TrainingProgramAdapter by lazy {
        TrainingProgramAdapter(context!!)
    }
    var program: TrainingProgram? = null
    private val viewModel: VMTrainingProgram by lazy {
        attachViewModel<VMTrainingProgram>(
            VMTrainingProgram::class.java
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout.setOnBackClick {
            controller.popBackStack()
        }
        viewModel.getProgramById(arguments?.getString("id"))
        viewModel.trainingProgram.observe(this, Observer {
            it?.let { resource ->
                if (resource.isSuccess()) {
                    program = resource.getSuccessResult()
                    initProgram(program)
                }
            }

        })
        if (onlyShow) btn_start_or_sub.hide()
        btn_start_or_sub.setOnClickListener {
            if (btn_start_or_sub.getText() == "Начать") {
                btn_start_or_sub.showProgress(true)
                viewModel.startTrainingSet()
            } else if (btn_start_or_sub.getText() == "Завершить") {
                btn_start_or_sub.showProgress(true)
                viewModel.endTrainingSet()
            } else {
                controller.navigate(R.id.globalChooseSubscriptionFragment)
            }

        }
        viewModel.message_start.observe(this, Observer {
            it?.let {message ->
                if (message =="failure")
                {
                    showInfoAlert(context!!,"Ошибка","Произошла ошибка выполнения запроса","ОК",{}).show()
                }
                else
                {
                    showInfoAlert(context!!,"Сообщение",message,"ОК",{}).show()
                }
                btn_start_or_sub.showProgress(false)
                viewModel.message_start.value = null
            }
        })
        viewModel.message_end.observe(this, Observer {
            it?.let {message ->
                if (message =="failure")
                {
                    showInfoAlert(context!!,"Ошибка","Произошла ошибка выполнения запроса","ОК",{}).show()
                }
                else
                {
                    showInfoAlert(context!!,"Сообщение",message,"ОК",{}).show()
                }
                btn_start_or_sub.showProgress(false)
                viewModel.message_end.value = null
            }
        })


    }

    @SuppressLint("SetTextI18n")
    private fun initProgram(program: TrainingProgram?) {
        txt_duration.text = program?.weeks_count?.let { getFormattedStringWeek(it) }
        txt_program_name.text = program?.name
        txt_goal.text = goals[program?.goals]
        txt_level.text = levels[program?.trainingLevel]
        txt_results.text = program?.description
        txt_results2.text = TextUtils.join(",",program?.inventories?.map { it?.name })
        txt_results3.text = "Общее число тренировок в программе - " + program?.trainingsTotal.toString()
        if (pref.training_sets.split(",").contains(program?.id)) {
            if (program?.available == true)
                btn_start_or_sub.setText("Завершить")
            else
                btn_start_or_sub.setText("Оформить подписку")
        } else if (program?.available == true)
            btn_start_or_sub.setText("Начать")
        else
            btn_start_or_sub.setText("Оформить подписку")
        val trainingList = arrayListOf<Training>()
        program?.weeks?.forEachIndexed() { index, currentWeek ->
            if (!currentWeek?.trainings.isNullOrEmpty())
                currentWeek?.trainings?.forEach { training ->
                    training?.weekTitle = "Неделя " + (index + 1).toString()
                    training?.weekDesc = ""
                    training?.let { trainingList.add(it) }
                }
        }

        rv_trainings_list.adapter = adapter
        adapter.data = trainingList as ArrayList<Any>
        adapter.notifyDataSetChanged()
        adapter.setItemSelectedCallback { training ->
            val b = Bundle()
            b.putSerializable("from", GO_TO_TRAINING.FROM_SET)
            training?.id?.let { b.putString("id", it) }
            b.putString("trainingData", Gson().toJson(training))
            controller.navigate(R.id.action_trainingProgramFragment_to_aboutTrainingFragment, b)
        }
    }

    class TrainingProgramAdapter(val context: Context) : BaseAdapter(R.layout.item_training) {


        private var itemSelectedCallback: (pos: Training?) -> Unit = {}
        private var index: Int = 1

        init {


            onBind { holder, pos ->
                val txtName = holder.find<TextView>(R.id.txt_section_name)
                val txtSectionDesc = holder.find<TextView>(R.id.txt_section_desc)
                val txtTrainingName = holder.find<TextView>(R.id.txt_training_name)
                val sectionHeaderView = holder.find<ConstraintLayout>(R.id.section_header_view)
                val sectionItemView = holder.find<ConstraintLayout>(R.id.section_item_view)
                val topView = holder.find<View>(R.id.view_top)
                val txtIndex = holder.find<TextView>(R.id.txt_index)

                val item = (data[pos]) as Training
                val list = data as ArrayList<Training>

                txtName.text = item.weekTitle
                txtSectionDesc.text = item.weekDesc
                txtTrainingName.text = item.name
                index += 1

                if (pos == 0)
                    topView.hide()

                if (pos > 0 && list[pos - 1].weekTitle == item.weekTitle) {
                    sectionHeaderView.hide()
                } else {
                    sectionHeaderView.show()
                    index = 1

                }
                txtIndex.text = index.toString()
                sectionItemView.setOnClickListener {
                    itemSelectedCallback(item)
                }

            }

        }

        fun setItemSelectedCallback(itemSelectedCallback: (pos: Training?) -> Unit) {
            this.itemSelectedCallback = itemSelectedCallback
        }


    }

    override fun onResume() {
        super.onResume()
        viewModel.userSubs.observe(this, Observer { resource ->
            if (resource.isSuccess()) {
                resource.getSuccessResult()?.let { list ->
                    if (list.isNotEmpty()) {
                        viewModel.getProgramById(arguments?.getString("id"))
                    }
                }
            }
        })
    }
}
