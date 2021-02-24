package ru.workout24.ui.workout_diary_planing_done.diary_planing_done_exercise

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

import ru.workout24.R
import ru.workout24.ui.workout_diary.data.dto.SingleExerciseDataDto
import ru.workout24.ui.workout_diary.data.dto.SingleExerciseDto
import ru.workout24.utills.*
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_diary_done_exercise_results.*
import org.jetbrains.anko.find

class DiaryDoneExerciseResultsFragment : BaseFragment(R.layout.fragment_diary_done_exercise_results) {

    private lateinit var result: SingleExerciseDto
    private var exercise: SingleExerciseDataDto? = null
    private val hasResult get() = arguments!!.getBoolean(HAS_RESULT, false)

    companion object {
        val HAS_RESULT = "HAS_RESULT"

        fun newInstance(res: SingleExerciseDto, hasResult: Boolean): DiaryDoneExerciseResultsFragment {
            val fragment = DiaryDoneExerciseResultsFragment()

            fragment.result = res
            fragment.exercise = res.singleExerciseData

            val args = Bundle().apply {
                putBoolean(HAS_RESULT, hasResult)
            }
            fragment.arguments = args

            return fragment
        }
    }

    private val adapter: DiaryDoneExerciseResultsAdapter by lazy {
        attachAdapter(DiaryDoneExerciseResultsAdapter(context!!))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (hasResult) {
            rv_results.adapter = adapter
            exercise?.let { exercise ->
                exercise.name?.let {name ->
                    adapter.data = arrayListOf(
                        DoneExerciseHeader(name, convertTimeToString(result.time!!.toLong())),
                        DoneExerciseItem("Подход 1/1", "x"+result.count.toString())

                    )
                }
            }
            adapter.notifyDataSetChanged()
        }
    }


    class DiaryDoneExerciseResultsAdapter(val context: Context) : BaseAdapter(R.layout.item_done_results) {


        init {


            onBind { holder, pos ->
                val clHeader = holder.find<ConstraintLayout>(R.id.cl_header)
                val tvHeaderText = holder.find<TextView>(R.id.tv_header_text)
                val tvHeaderTime = holder.find<TextView>(R.id.tv_header_time)
                val clItem = holder.find<ConstraintLayout>(R.id.cl_item)
                val tvLeft = holder.find<TextView>(R.id.tv_left)
                val tvRight = holder.find<TextView>(R.id.tv_right)

                val item = (data[pos]) as DoneExerciseAdapterItem
                clHeader.hide()
                clItem.hide()

                when (item) {
                    is DoneExerciseItem -> {
                        clItem.show()
                        tvLeft.text = item.leftText
                        tvRight.text = item.rightText
                    }
                    is DoneExerciseHeader -> {
                        clHeader.show()
                        tvHeaderText.text = item.headerText
                        tvHeaderTime.text = item.timeText
                    }
                }
            }


        }

    }

    interface DoneExerciseAdapterItem

    data class DoneExerciseItem(
        val leftText: String,
        val rightText: String
    ) : DoneExerciseAdapterItem

    data class DoneExerciseHeader(
        val headerText: String,
        val timeText: String
    ) : DoneExerciseAdapterItem


}