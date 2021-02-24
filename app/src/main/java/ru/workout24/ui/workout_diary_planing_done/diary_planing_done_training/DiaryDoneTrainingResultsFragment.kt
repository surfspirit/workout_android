package ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import ru.workout24.R
import ru.workout24.network.Api
import ru.workout24.network.ResourceProvider
import ru.workout24.ui.workout_diary.data.dto.TrainingDto
import ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training.mapper.TrainingResultMapper
import ru.workout24.utills.*
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_diary_done_training_results.*
import org.jetbrains.anko.find
import javax.inject.Inject

class DiaryDoneTrainingResultsFragment :
    BaseFragment(R.layout.fragment_diary_done_training_results) {
    @Inject
    lateinit var api: Api
    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val trainingDto get() = arguments!!.getParcelable<TrainingDto>(DiaryPlaningDoneTrainingFragment.TRAINING_KEY)
    private val trainingId get() = trainingDto.trainingData!!.id
    private val assignmentId get() = trainingDto.assignmentId
    private val hasResult get() = arguments!!.getBoolean(HAS_RESULT, false)

    private val viewModel by lazy {
        ViewModelProviders.of(
            this,
            VMDiaryDoneTrainingFactory(api, resourceProvider, trainingId, assignmentId)
        ).get(VMDiaryDoneTraining::class.java)
    }

    companion object {
        val HAS_RESULT = "HAS_RESULT"

        fun newInstance(hasResult: Boolean, data: TrainingDto): DiaryDoneTrainingResultsFragment {
            val fragment = DiaryDoneTrainingResultsFragment()
            fragment.arguments = Bundle().apply {
                putBoolean(HAS_RESULT, hasResult)
                putParcelable(DiaryPlaningDoneTrainingFragment.TRAINING_KEY, data)
            }
            return fragment
        }
    }

    private val adapter: DiaryDoneTrainingResultsAdapter by lazy {
        attachAdapter(DiaryDoneTrainingResultsAdapter(context!!))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (hasResult) {
            viewModel.trainingLiveData.observe(this, Observer { result ->
                adapter.data = ArrayList(TrainingResultMapper().apply(result))
                adapter.notifyDataSetChanged()
            })
            viewModel.load()
            rv_results.adapter = adapter
        }
    }


    class DiaryDoneTrainingResultsAdapter(val context: Context) :
        BaseAdapter(R.layout.item_done_results) {


        init {


            onBind { holder, pos ->
                val clHeader = holder.find<ConstraintLayout>(R.id.cl_header)
                val tvHeaderText = holder.find<TextView>(R.id.tv_header_text)
                val tvHeaderTime = holder.find<TextView>(R.id.tv_header_time)
                val clItem = holder.find<ConstraintLayout>(R.id.cl_item)
                val tvLeft = holder.find<TextView>(R.id.tv_left)
                val tvRight = holder.find<TextView>(R.id.tv_right)

                val item = (data[pos]) as DoneTrainingAdapterItem
                clHeader.hide()
                clItem.hide()

                when (item) {
                    is DoneTrainingItem -> {
                        clItem.show()
                        tvLeft.text = item.leftText
                        tvRight.text = item.rightText
                    }
                    is DoneTrainingHeader -> {
                        clHeader.show()
                        tvHeaderText.text = item.headerText
                        tvHeaderTime.text = item.timeText
                    }
                }
            }


        }

    }

    interface DoneTrainingAdapterItem

    data class DoneTrainingItem(
        val leftText: String,
        val rightText: String
    ) : DoneTrainingAdapterItem

    data class DoneTrainingHeader(
        val headerText: String,
        val timeText: String
    ) : DoneTrainingAdapterItem


}