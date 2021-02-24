package ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import ru.workout24.R
import ru.workout24.network.Api
import ru.workout24.network.ResourceProvider
import ru.workout24.ui.trainings.training_programs.pojos.ExerciseTrainingProgram
import ru.workout24.ui.trainings.training_programs.pojos.Training
import ru.workout24.ui.workout_diary.data.dto.TrainingDto
import ru.workout24.utills.*
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_diary_done_training_plan.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.toast
import ru.workout24.ui.exercises_execution.ExercisesExecutionHostFragment
import javax.inject.Inject

class DiaryDoneTrainingPlanFragment : BaseFragment(R.layout.fragment_diary_done_training_plan) {

    @Inject
    lateinit var api: Api

    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val trainingDto
        get() = arguments!!.getParcelable<TrainingDto>(
            DiaryPlaningDoneTrainingFragment.TRAINING_KEY
        )
    private val trainingId get() = trainingDto.trainingData!!.id
    private val assignmentId get() = trainingDto.assignmentId
    private val hasResult
        get() = arguments!!.getBoolean(
            DiaryDoneTrainingResultsFragment.HAS_RESULT,
            false
        )
    private val viewModel by lazy {
        ViewModelProviders.of(
            this,
            VMDiaryDoneTrainingFactory(api, resourceProvider, trainingId, assignmentId)
        ).get(VMDiaryDoneTraining::class.java)
    }
    private val adapter: AboutTrainingAdapter by lazy {
        AboutTrainingAdapter(context!!)
    }

    companion object {
        fun newInstance(hasResult: Boolean, data: TrainingDto): DiaryDoneTrainingPlanFragment {
            val fragment = DiaryDoneTrainingPlanFragment()
            fragment.arguments = Bundle().apply {
                putBoolean(DiaryDoneTrainingResultsFragment.HAS_RESULT, hasResult)
                putParcelable(DiaryPlaningDoneTrainingFragment.TRAINING_KEY, data)
            }
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.trainingLiveData.observe(this, Observer { training ->
            initTraining(training.first)
        })
        viewModel.load()

        viewModel.startTrainingAssignedId.observe(this) { assignmentId ->
            viewModel.trainingLiveData.value?.first?.sets?.let {
                controller.navigate(
                    R.id.action_global_exercisesExecutionHostFragment,
                    ExercisesExecutionHostFragment.openForTraining(
                        assignmentId,
                        it.filterNotNull().toArrayList()
                    )
                )
            }
        }
        viewModel.startTrainingAssignedError.observe(this) {
           toast(it)
        }
        // TODO: раскоментить когда будет состыкована группа экранов для прохождения тренировки
        if (!hasResult) {
            btn_start.show()
            btn_start.setOnClickListener {
                viewModel.startTraining(trainingId)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initTraining(training: Training?) {
        txt_program_name.text = training?.name
        txt_goal.text = goals[training?.goals]
        txt_level.text = levels[training?.trainingLevel]
        txt_results.text = training?.description
        training?.inventories?.let {
            if (it.isNotEmpty()) {
                txt_results2.text = TextUtils.join(",", it.map { it?.name })
            }
        }
        txt_results3.text =
            "Приблизительное время тренировки " + training?.estimateDuration?.div(60) + " мин."
        tv_time.text = transformStringToTimeFromISO(trainingDto.startDate)
        tv_date.text = transformStringDateWordingFromISO(trainingDto.startDate)
        val exerciseList = arrayListOf<ExerciseTrainingProgram>()
        training?.sets?.forEachIndexed() { index, currentSet ->
            if (!currentSet?.exercises.isNullOrEmpty())
                currentSet?.exercises?.forEach { exercise ->
                    exercise?.setTitle =
                        "Подход " + (index + 1).toString() + "/" + training.sets.size
                    exercise?.setDesc = ""
                    exercise?.let { exerciseList.add(it) }
                }
        }

        rv_sets_list.adapter = adapter
        adapter.data = exerciseList as ArrayList<Any>
        adapter.notifyDataSetChanged()
        /*adapter.setItemSelectedCallback { exercise ->
            if (trainingData?.available == true) {
                val b = Bundle()
                b.putString("exercise",Gson().toJson(exercise))
                b.putSerializable("from",from)
                controller.navigate(R.id.exerciseDescriptionFragment,b)
            }
        }*/
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

                //if (trainingData?.available == true) {
                playIcon.show()
                lock.hide()
//                } else {
//                    playIcon.hide()
//                    lock.show()
//                    iv_preview.setColorFilter(
//                        ContextCompat.getColor(
//                            context,
//                            R.color.transparentBlack
//                        )
//                    );
//                }
                sectionItemView.setOnClickListener {

                    itemSelectedCallback((data[pos]) as ExerciseTrainingProgram)
                }
            }
        }

        fun setItemSelectedCallback(itemSelectedCallback: (pos: ExerciseTrainingProgram?) -> Unit) {
            this.itemSelectedCallback = itemSelectedCallback
        }
    }
}