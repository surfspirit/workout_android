package ru.workout24.ui.workout_diary_planing_done.diary_planing_done_exercise

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView

import ru.workout24.R
import ru.workout24.ui.workout_diary.data.dto.SingleExerciseDataDto
import ru.workout24.ui.workout_diary.data.dto.SingleExerciseDto
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.getCountByRange
import ru.workout24.utills.hide
import ru.workout24.utills.show
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_diary_done_exercise_plan.*
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class DiaryDoneExercisePlanFragment : BaseFragment(R.layout.fragment_diary_done_exercise_plan) {

    private val adapter: ExerciseAttrs by lazy {
        attachAdapter(ExerciseAttrs(context!!))
    }

    private lateinit var result: SingleExerciseDto
    private var exercise: SingleExerciseDataDto? = null

    companion object {
        fun newInstance(res: SingleExerciseDto): DiaryDoneExercisePlanFragment {
            val fragment = DiaryDoneExercisePlanFragment()
            fragment.result = res
            fragment.exercise = res.singleExerciseData
            val args = Bundle()
            fragment.arguments = args

            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iv_muscles.hide()
        iv_muscles2.hide()
        exercise?.let { exercise ->
            playerView.initVimeoPlayer(exercise.videoUrl + "?autoplay=1&amp;title=1&amp;byline=1&amp;portrait=0")
            Glide.with(activity?.applicationContext!!).load(exercise.previewUrl).into(iv_preview)
            exercise.muscleGroups?.let {
                it.forEachIndexed { index, muscleGroup ->
                    when (index) {
                        0 -> {
                            iv_muscles.show()
                            glide.load(exercise.muscleGroups.get(index).previewUrl)
                                .into(iv_muscles)
                        }
                        1 -> {
                            iv_muscles2.show()
                            glide.load(exercise.muscleGroups.get(index).previewUrl)
                                .into(iv_muscles2)
                        }

                    }
                }
            }
            glide.load(exercise.muscleGroups?.get(0)?.previewUrl).into(iv_muscles)
            val list = arrayListOf<SimpleValue>()
            list.add(
                SimpleValue(
                    "Повторов",
                    getCountByRange(exercise.requiredRange?.min, exercise.requiredRange?.max)
                )
            )
            list.add(SimpleValue("Инвентарь", exercise.inventories?.map { it.name }?.joinToString()))
            exercise.muscleGroups?.let {
                txt_muscles.text =
                    "Работа мышц: " + exercise.muscleGroups?.map { it.name }?.joinToString("+")

            }
            exercise.estimate_time?.let { time ->
                txt_time.text = String.format(
                    "%d мин",
                    TimeUnit.SECONDS.toMinutes(time.toLong()) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.SECONDS.toHours(
                            time.toLong()
                        )
                    )
                )
            }
            rv_exercise_attrs.adapter = adapter
            adapter.data = list as ArrayList<Any>
            adapter.notifyDataSetChanged()
            txt_ex_desc.text = exercise.description
            txt_ex_name.text = exercise.name
        }
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS").parse(result.startDate)
        tv_ex_time.text = SimpleDateFormat("HH:mm").format(date)
        tv_ex_date.text = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(date)
    }

    inner class ExerciseAttrs(val context: Context) : BaseAdapter(R.layout.exercise_attr) {

        init {

            count = 2

            onBind { holder, pos ->

                val title = holder.find<TextView>(R.id.txt_attr_title)
                val value = holder.find<TextView>(R.id.txt_attr_value)
                val v = data[pos] as SimpleValue
                title.text = v.key
                value.text = v.value

            }
        }

    }

    data class SimpleValue(val key: String, val value: String?)


}