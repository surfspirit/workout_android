package ru.workout24.ui.workout_diary


import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.collapsiblecalendarview.widget.CollapsibleCalendar
import kotlinx.android.synthetic.main.fragment_workout_diary.*
import org.jetbrains.anko.backgroundColor
import ru.workout24.R
import ru.workout24.network.Api
import ru.workout24.network.ResourceProvider
import ru.workout24.ui.workout_diary.adapter.HolderType
import ru.workout24.ui.workout_diary.adapter.OnItemListener
import ru.workout24.ui.workout_diary.adapter.WorkoutDiaryAdapter
import ru.workout24.ui.workout_diary.data.dto.SingleExerciseDto
import ru.workout24.ui.workout_diary.data.dto.TrainingDto
import ru.workout24.ui.workout_diary.data.model.WorkoutViewModel.*
import ru.workout24.ui.workout_diary.options_dialog.OptionsDialogFragment
import ru.workout24.ui.workout_diary.options_dialog.OptionsDialogListener
import ru.workout24.ui.workout_diary_planing_done.diary_planing_done_exercise.DiaryPlaningDoneExerciseFragment
import ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training.DiaryPlaningDoneTrainingFragment
import ru.workout24.utills.SwipeGestureListener
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel
import ru.workout24.utills.custom_views.TypeDivider
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class WorkoutDiaryFragment : BaseFragment(R.layout.fragment_workout_diary), OptionsDialogListener {
    private val CALENDAR_EXPAND_DURATION = 150

    @Inject
    lateinit var resourceProvider: ResourceProvider
    @Inject
    lateinit var api: Api

    private var calendarMode = CalendarMode.WEEK
        set(value) {
            field = value
            viewModel.changeCalendarMode(value)
        }

    private val scrollListener = ViewTreeObserver.OnScrollChangedListener {
        //TODO: сделать нормальное удаление листенера
        if (scrollContainer != null && scrollContainer.scrollY > 0) {
            appBar?.backgroundColor =
                ContextCompat.getColor(requireContext(), R.color.black)
        } else {
            appBar?.backgroundColor = Color.TRANSPARENT
        }
    }
    private val viewModel by lazy {
        ViewModelProviders.of(
            this,
            VMWorkoutDiaryFactory(
                resourceProvider,
                api
            )
        )[VMWorkoutDiary::class.java]
    }
    private val adapter by lazy {
        WorkoutDiaryAdapter().apply {
            useDiffUtil = true
        }
    }
    private val calendarListener = object : CollapsibleCalendar.CalendarListener {
        override fun onMonthChange() {
            setTitleMonthYear()
        }

        override fun onDataUpdate() {
        }

        override fun onWeekChange(position: Int) {
        }

        override fun onDaySelect() {
        }

        override fun onItemClick(v: View?) {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBar.setOnBackClick {
            controller.popBackStack()
        }
        setupCalendarUi()
        setupRecycler()
        setTitleMonthYear()
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.calendarData.observe(viewLifecycleOwner, Observer { pair ->
            val items = pair.first
            val calendarTags = pair.second
            adapter.setData(ArrayList(items))
            addCalendarEventsTag(calendarTags)
        })
        viewModel.changeCalendarMonthYear()
        viewModel.singleMessage.observe(this) {
            showInfoAlert(requireContext(), "", it, "Ок", {}).showIfNeeded()
        }
    }

    private fun setupRecycler() {
        adapter.setOnOptionListener(object : OnItemListener {
            override fun onItemClick(item: AbstractTypeViewModel) {
                when (item.type) {
                    HolderType.SINGLE_TRAINING.value, HolderType.PROGRAM_TRAINING.value -> {
                        item as TrainingViewModel
                        val training = item.value as TrainingDto
                        controller.navigate(
                            R.id.action_workout_diary_to_diaryDoneTrainingFragment,
                            DiaryPlaningDoneTrainingFragment.getBundle(
                                item.trainingType,
                                training
                            )
                        )
                    }
                    HolderType.SINGLE_EXERCISE.value -> {
                        item as TrainingViewModel
                        controller.navigate(
                            R.id.action_workout_diary_to_diaryDoneExerciseFragment,
                            DiaryPlaningDoneExerciseFragment.getBundle(
                                item.value as SingleExerciseDto,
                                item.trainingType
                            )
                        )
                    }
                }
            }

            override fun onOptionClick(view: View, item: AbstractTypeViewModel) {
                showOptionDialog(view, item)
            }
        })
        rv_workout.adapter = adapter
        rv_workout.addItemDecoration(
            TypeDivider(
                HolderType.PROGRAM_TRAINING.value,
                requireContext().resources.getDimensionPixelSize(R.dimen.divider_height),
                ContextCompat.getColor(requireContext(), R.color.whiteThree),
                requireContext().resources.getDimensionPixelSize(R.dimen.divider_left_margin)
            )
        )
        rv_workout.addItemDecoration(
            TypeDivider(
                HolderType.PROGRAM_HEADER.value,
                requireContext().resources.getDimensionPixelSize(R.dimen.divider_height),
                ContextCompat.getColor(requireContext(), R.color.whiteThree),
                requireContext().resources.getDimensionPixelSize(R.dimen.divider_left_margin)
            )
        )
    }

    private fun setTitleMonthYear() {
        val month = ruMonths[calendar.month]
        val year = calendar.year.toString()
        title.text = createTitle(month, year)
    }

    private fun setupCalendarUi() {
        calendar.setExpandIconVisible(false)
        calendar.setButtonLeftVisibility(View.GONE)
        calendar.setButtonRightVisibility(View.GONE)
        calendar.setMonthAndYearVisibility(View.GONE)
        calendar.setCalendarListener(calendarListener)
        changeCalendarMode(calendarMode, false)
        calendar.setOnTouchListener(object : SwipeGestureListener(requireContext()) {
            override fun onSwipeLeft() {
                when (calendarMode) {
                    CalendarMode.WEEK -> {
                        calendar.nextWeek()
                        viewModel.nextWeek()
                    }
                    CalendarMode.MONTH -> {
                        calendar.nextMonth()
                        viewModel.nextMonth()
                    }
                }
            }

            override fun onSwipeRight() {
                when (calendarMode) {
                    CalendarMode.WEEK -> {
                        calendar.prevWeek()
                        viewModel.prevWeek()
                    }
                    CalendarMode.MONTH -> {
                        calendar.prevMonth()
                        viewModel.prevMonth()
                    }
                }
            }

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                // блокируем скролл вью при нажатии на календарь, чтобы свайп влево и вправо по календарю не лагали
                scrollContainer.isLocked =
                    event?.action == MotionEvent.ACTION_DOWN || event?.action == MotionEvent.ACTION_MOVE
                return super.onTouch(v, event)
            }
        })
        calendarSelectable.setInitMode(calendarMode)
        calendarSelectable.setCalendarModeListener { mode ->
            calendarMode = mode
            changeCalendarMode(calendarMode, true)
        }
        scrollContainer.viewTreeObserver.addOnScrollChangedListener(scrollListener)
    }

    private fun changeCalendarMode(mode: CalendarMode, refresh: Boolean) {
        when (mode) {
            CalendarMode.WEEK -> {
                calendar.collapse(CALENDAR_EXPAND_DURATION)
//                if (refresh) viewModel.weekCalendarMode()
            }
            CalendarMode.MONTH -> {
                calendar.expand(CALENDAR_EXPAND_DURATION)
            }
        }
    }

    private fun addCalendarEventsTag(set: Set<Calendar>) {
        val color = ContextCompat.getColor(requireContext(), R.color.tomato)
        set.forEach { cal ->
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)
            calendar.addEventTag(
                year, month, day, color,
                Color.WHITE
            )
        }
    }

    private fun createTitle(month: String, year: String): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        builder.append(month)
        builder.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.coral
                )
            ),
            0,
            month.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.append(" ")
        builder.append(year)
        return builder
    }

    private fun showOptionDialog(view: View, item: AbstractTypeViewModel) {
        val fragment = when (item) {
            is ProgramHeaderViewModel -> OptionsDialogFragment.newInstanceForProgram(view, item)
            is TrainingViewModel -> OptionsDialogFragment.newInstanceForTraining(
                view,
                item.modelType,
                item.trainingType,
                item
            )
            else -> null
        }
        fragment?.show(childFragmentManager, "optionDialog")
        fragment?.setOptionDialogListener(this)
    }

    override fun seeProgram(item: ProgramHeaderViewModel) {
        controller.navigate(
            R.id.action_workout_diary_to_trainingProgram,
            Bundle().apply { putString("id", item.programId) })
    }

    override fun endProgram(item: ProgramHeaderViewModel) {
        showActionAlert(
            requireContext(),
            "Завершить программу",
            "Хотите завершить программу и удалить её из списка?",
            "Да",
            "Отмена",
            { viewModel.endProgram(item) }).show()
    }

    override fun seeExerciseResult(item: TrainingViewModel) {
        controller.navigate(
            R.id.action_workout_diary_to_diaryDoneExerciseFragment,
            DiaryPlaningDoneExerciseFragment.getBundle(
                item.value as SingleExerciseDto,
                item.trainingType
            )
        )
    }

    override fun seeSingleTrainingResult(item: TrainingViewModel) {
        val training = item.value as TrainingDto
        controller.navigate(
            R.id.action_workout_diary_to_diaryDoneTrainingFragment,
            DiaryPlaningDoneTrainingFragment.getBundle(item.trainingType, training)
        )
    }

    override fun seeUpcomingProgramTraining(item: TrainingViewModel) {
        val training = item.value as TrainingDto
        controller.navigate(
            R.id.action_workout_diary_to_diaryDoneTrainingFragment,
            DiaryPlaningDoneTrainingFragment.getBundle(item.trainingType, training)
        )
    }

    override fun seeDoneProgramTrainingResult(item: TrainingViewModel) {
        val training = item.value as TrainingDto
        controller.navigate(
            R.id.action_workout_diary_to_diaryDoneTrainingFragment,
            DiaryPlaningDoneTrainingFragment.getBundle(item.trainingType, training)
        )
    }

    override fun moveLostProgramTraining(item: TrainingViewModel) {
        viewModel.moveLostProgramTraining(item)
    }

    override fun removeProgramTraining(item: TrainingViewModel) {
        viewModel.removeProgramTraining(item)
    }

    override fun seeTrainingProgram(item: TrainingViewModel) {
        val training = item.value as TrainingDto
        training.programData?.let {
            controller.navigate(
                R.id.action_workout_diary_to_trainingProgram,
                Bundle().apply { putString("id", it.programId) })
        }
    }

    private var ruMonths = arrayOf(
        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Августа", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    )
}
