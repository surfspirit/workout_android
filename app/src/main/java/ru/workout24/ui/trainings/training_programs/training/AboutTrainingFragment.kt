package ru.workout24.ui.trainings.training_programs.training


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import ru.workout24.R
import ru.workout24.ui.trainings.training_programs.pojos.ExerciseTrainingProgram
import ru.workout24.ui.trainings.training_programs.pojos.Training
import ru.workout24.utills.*
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_about_training.*
import kotlinx.android.synthetic.main.fragment_training_program.appBarLayout
import kotlinx.android.synthetic.main.layout_finish_training.*
import org.jetbrains.anko.find
import ru.workout24.ui.exercises_execution.ExercisesExecutionHostFragment

class AboutTrainingFragment : BaseFragment(R.layout.fragment_about_training) {
    private val onlyShow get() = arguments?.getBoolean("onlyShow", false) ?: false

    private val adapter: AboutTrainingAdapter by lazy {
        attachAdapter(AboutTrainingAdapter(context!!))
    }
    private val viewModel: VMAboutTraining by lazy {
        attachViewModel<VMAboutTraining>(
            VMAboutTraining::class.java
        )
    }
    var training: Training? = null
    var from: GO_TO_TRAINING? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout.setOnBackClick {
            controller.popBackStack()
        }
        val id = arguments?.getString("id")
        from = arguments?.getSerializable("from") as GO_TO_TRAINING
        if (from == GO_TO_TRAINING.FROM_SET) {
            viewModel.getTrainingById(id)
//            training = Gson().fromJson(arguments?.getString("trainingData"), Training::class.java)
        }
        if (from == GO_TO_TRAINING.FROM_ONCE_TRAININGS) {
            //TODO:придумать, как сделать по нормальному, а не через костыль
            viewModel.getTrainingById(id)

//            training = Gson().fromJson(arguments?.getString("trainingData"), Training::class.java)
        }

        viewModel.training.observe(this, Observer { resource ->
            if (resource.isSuccess()) {
                training = resource.getSuccessResult()
                initTraining(training)
            }
            btn_start_or_sub.isEnable = true
            btn_start_or_sub.setOnClickListener {
                if (btn_start_or_sub.getText() == "Начать") {
                    if (training?.setsCount==0)
                    {
                        showInfoAlert(context!!,"Сообщение","В тренировке нет упражнений","ОК",{}).show()
                    }
                    else {
                        btn_start_or_sub.showProgress(true)
                        viewModel.startTraining()
                    }
                } else {
                    controller.navigate(R.id.globalChooseSubscriptionFragment)
                }
            }
        })
        btn_start_or_sub.isEnable = false
        if (onlyShow) btn_start_or_sub.hide()
        viewModel.message.observe(this, Observer {
            it?.let { message ->
                btn_start_or_sub.showProgress(false)
                if (message == "failure")
                    showInfoAlert(context!!,"Ошибка","Произошла ошибка выполнения запроса","ОК",{}).show()
                else {
                    when(from){
                        GO_TO_TRAINING.FROM_ONCE_TRAININGS -> {
                            training?.sets?.filterNotNull()?.toAL()?.let { sets ->
                                controller.navigate(R.id.action_global_exercisesExecutionHostFragment, ExercisesExecutionHostFragment.openForTraining(message, sets))
                            }
                        }
                        GO_TO_TRAINING.FROM_SET -> {
                            training?.sets?.filterNotNull()?.toAL()?.let { sets ->
                                controller.navigate(R.id.action_global_exercisesExecutionHostFragment, ExercisesExecutionHostFragment.openForProgramTraining(message, sets))
                            }
                        }
                    }
                }
                viewModel.message.value = null
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initTraining(training: Training?) {
        txt_program_name.text = training?.name
        txt_goal.text = goals[training?.goals]
        txt_level.text = levels[training?.trainingLevel]
        txt_results.text = training?.description
        txt_results2.text = TextUtils.join(",",training?.inventories?.map { it?.name })
        txt_results3.text =
            "Приблизительное время тренировки " + training?.estimateDuration?.div(60) + " мин."
        if (training?.available == true)
            btn_start_or_sub.setText("Начать")
        else btn_start_or_sub.setText("Оформить подписку")
        txt_section_desc.text = "Переходите к следующей тренировке"

        val exerciseList = arrayListOf<ExerciseTrainingProgram>()
        training?.sets?.forEachIndexed() { index, currentSet ->
            if (!currentSet?.exercises.isNullOrEmpty())
                currentSet?.exercises?.forEach { exercise ->
                    exercise?.setTitle = "Подход " + (index + 1).toString() + "/" + training.sets.size
                    exercise?.setDesc = ""
                    exercise?.let { exerciseList.add(it) }
                }
        }

        rv_sets_list.adapter = adapter
        adapter.data = exerciseList as ArrayList<Any>
        adapter.notifyDataSetChanged()
        adapter.setItemSelectedCallback { exercise ->
            if (training?.available == true) {
                val b = Bundle()
                b.putString("exercise",Gson().toJson(exercise))
                b.putSerializable("from",from)
                controller.navigate(R.id.exerciseDescriptionFragment,b)
            }
        }
    }

    inner class AboutTrainingAdapter(val context: Context) : BaseAdapter(R.layout.item_set) {


        private var itemSelectedCallback: (pos: ExerciseTrainingProgram?) -> Unit = {}

        init {


            onBind { holder, pos ->
                val txtName = holder.find<TextView>(R.id.txt_section_name)
                val txtSectionDesc = holder.find<TextView>(R.id.txt_section_desc)
                val txtTrainingName = holder.find<TextView>(R.id.txt_training_name)
                val sectionHeaderView = holder.find<ConstraintLayout>(R.id.section_header_view)
                val sectionItemView = holder.find<ConstraintLayout>(R.id.section_item_view)
                val topView = holder.find<View>(R.id.view_top)
                val playIcon = holder.find<ImageView>(R.id.iv_play_icon)
                val lock = holder.find<ImageView>(R.id.iv_lock)
                val iv_preview = holder.find<ImageView>(R.id.iv_preview)

                val item = (data[pos]) as ExerciseTrainingProgram
                val list = data as ArrayList<ExerciseTrainingProgram>

                txtName.text = item.setTitle
                txtSectionDesc.text = item.setDesc
                txtTrainingName.text = item.name
                glide?.load(item.previewUrl)?.into(iv_preview)
                if (pos == 0)
                    topView.hide()

                if (pos > 0 && list[pos - 1].setTitle == item.setTitle) {
                    sectionHeaderView.hide()
                } else {
                    sectionHeaderView.show()
                }

                if (training?.available == true) {
                    playIcon.show()
                    lock.hide()
                } else {
                    playIcon.hide()
                    lock.show()
                    iv_preview.setColorFilter(ContextCompat.getColor(context,R.color.transparentBlack));
                }
                sectionItemView.setOnClickListener {

                    itemSelectedCallback( (data[pos]) as ExerciseTrainingProgram)
                }

            }

        }

        fun setItemSelectedCallback(itemSelectedCallback: (pos: ExerciseTrainingProgram?) -> Unit) {
            this.itemSelectedCallback = itemSelectedCallback
        }


    }

    override fun onResume() {
        super.onResume()
        resourceFactory.userResource.load()
        resourceFactory.userResource.onChange(this, {user ->
            if (user.have_subscription)
                viewModel.getTrainingById(arguments?.getString("id"))
        }, {
            showInfoAlert(context!!,"Ошибка","Произошла ошибка выполнения запроса","ОК",{}).show()
        })
    }
}
