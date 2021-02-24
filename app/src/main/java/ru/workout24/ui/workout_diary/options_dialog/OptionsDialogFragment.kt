package ru.workout24.ui.workout_diary.options_dialog

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import ru.workout24.R
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import ru.workout24.ui.workout_diary.adapter.HolderType
import ru.workout24.ui.workout_diary.data.model.WorkoutViewModel.*
import ru.workout24.ui.workout_diary.data.model.TrainingType
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find
import ru.workout24.utills.show


class OptionsDialogFragment : DialogFragment() {
    private val centerY get() = arguments?.getInt(VIEW_CENTER_Y_KEY)!!
    private val viewX get() = arguments?.getInt(VIEW_X_KEY)!!
    private val holderType get() = arguments?.getSerializable(HOLDER_TYPE_KEY) as HolderType
    private val trainingType get() = arguments?.getSerializable(TRANING_TYPE_KEY) as TrainingType
    private val program get() = arguments!!.getParcelable<ProgramHeaderViewModel>(ITEM_KEY)
    private val training get() = arguments!!.getParcelable<TrainingViewModel>(ITEM_KEY)

    private var optionsListener: OptionsDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.OptionsDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.apply {
            // окно на весь экран
            setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )
            // прозрачный статус бар
            statusBarColor = Color.TRANSPARENT
        }
        // формируем полупрозрачный задник
        // без него заинфлейченая вью растянется по всему экрану
        val view = FrameLayout(requireContext())
        view.backgroundColor = ContextCompat.getColor(requireContext(), R.color.transparentWhite)
        view.setOnClickListener {
            dismiss()
        }
        view.setupOptionDialogViewPositionOnScreen()
        val optionView = inflater.inflate(R.layout.fragment_option_dialog, container, false)
        optionView.setupOptionDialogView()
        view.addView(optionView)
        return view
    }

    private fun View.setupOptionDialogViewPositionOnScreen() {
        val containerWidth = resources.getDimensionPixelSize(R.dimen.options_dialog_width)
        val containerButtonHeight =
            resources.getDimensionPixelSize(R.dimen.options_dialog_button_height)
        val hasOptionalButton =
            holderType == HolderType.PROGRAM_TRAINING && (trainingType == TrainingType.UPCOMING || trainingType == TrainingType.LOST)
        val calculateHalfOptionHeight =
            if (hasOptionalButton) containerButtonHeight + containerButtonHeight / 2 else containerButtonHeight
        val containerTopMargin = centerY - calculateHalfOptionHeight
        val containerLeftMargin = viewX - containerWidth
        this.setPadding(containerLeftMargin, containerTopMargin, 0, 0)
    }

    private fun View.setupOptionDialogView() {
        val containerButtonHeight = resources.getDimensionPixelSize(R.dimen.options_dialog_button_height)
        val hasOptionalButton = holderType == HolderType.PROGRAM_TRAINING && (trainingType == TrainingType.UPCOMING || trainingType == TrainingType.LOST)
        val containerWidth = resources.getDimensionPixelSize(R.dimen.options_dialog_width)
        val containerHeight = if (hasOptionalButton) containerButtonHeight * 3 else containerButtonHeight * 2
        val containerParams = ViewGroup.MarginLayoutParams(containerWidth, containerHeight)
        layoutParams = containerParams

        when (holderType) {
            HolderType.PROGRAM_HEADER -> {
                if(program.isDone) {
                    setupOptionsButtons(
                        context.getString(R.string.see_program),
                        context.getString(R.string.cancel),
                        {
                            optionsListener?.endProgram(program)
                        },
                        {})
                } else {
                    setupOptionsButtons(
                        context.getString(R.string.end_program),
                        context.getString(R.string.see_program),
                        {
                            optionsListener?.endProgram(program)
                        },
                        {
                            optionsListener?.seeProgram(program)
                        })
                }
            }
            HolderType.SINGLE_EXERCISE -> {
                setupOptionsButtons(
                    context.getString(R.string.see_result),
                    context.getString(R.string.cancel),
                    { optionsListener?.seeExerciseResult(training) },
                    {}
                )
            }
            HolderType.SINGLE_TRAINING -> {
                setupOptionsButtons(
                    context.getString(R.string.see_result),
                    context.getString(R.string.cancel),
                    { optionsListener?.seeSingleTrainingResult(training) },
                    {}
                )
            }
            HolderType.PROGRAM_TRAINING -> {
                when(trainingType) {
                    TrainingType.UPCOMING -> {
                        setupOptionsButtons(
                            context.getString(R.string.see_training),
                            context.getString(R.string.see_program),
                            { optionsListener?.seeUpcomingProgramTraining(training) },
                            { optionsListener?.seeTrainingProgram(training) },
                            context.getString(R.string.delete_from_list),
                            { optionsListener?.removeProgramTraining(training) }
                        )
                    }
                    TrainingType.DONE -> {
                        setupOptionsButtons(
                            context.getString(R.string.see_result),
                            context.getString(R.string.see_program),
                            { optionsListener?.seeDoneProgramTrainingResult(training) },
                            { optionsListener?.seeTrainingProgram(training) }
                        )
                    }
                    TrainingType.LOST -> {
                        setupOptionsButtons(
                            context.getString(R.string.on_next_week),
                            context.getString(R.string.see_program),
                            { optionsListener?.moveLostProgramTraining(training) },
                            { optionsListener?.seeTrainingProgram(training) }
                        )
                    }
                }
            }
        }
    }

    private fun View.setupOptionsButtons(
        topButtonText: String,
        bottomButtonText: String,
        topListener: () -> Unit,
        bottomListener: () -> Unit,
        optionalButton: String? = null,
        optionalListener: () -> Unit = {}
    ) {
        val top = find<TextView>(R.id.topButton)
        val bottom = find<TextView>(R.id.bottomButton)
        val optional = find<TextView>(R.id.optionalButton)
        val optionalDivider = find<View>(R.id.dividerOptional)
        top.text = topButtonText
        top.setOnClickListener {
            topListener()
            dismiss()
        }
        bottom.text = bottomButtonText
        bottom.setOnClickListener {
            bottomListener()
            dismiss()
        }
        optionalButton?.let {
            optional.show()
            optionalDivider.show()
            optional.text = it
            optional.setOnClickListener {
                optionalListener()
                dismiss()
            }
        }
    }

    fun setOptionDialogListener(listener: OptionsDialogListener) {
        optionsListener = listener
    }

    companion object {
        val VIEW_CENTER_Y_KEY = "VIEW_CENTER_Y_KEY"
        val VIEW_X_KEY = "VIEW_X_KEY"
        val HOLDER_TYPE_KEY = "HOLDER_TYPE_KEY"
        val TRANING_TYPE_KEY = "TRANING_TYPE_KEY"
        val ITEM_KEY = "ITEM_KEY"

        private fun createBundle(view: View): Bundle {
            val viewLocation = IntArray(2)
            view.getLocationOnScreen(viewLocation)
            val x = viewLocation[0]
            val y = viewLocation[1]
            val centerY = y + view.height / 2

            val bundle = Bundle()
            bundle.putInt(VIEW_CENTER_Y_KEY, centerY)
            bundle.putInt(VIEW_X_KEY, x)
            return bundle
        }

        private fun newInstance(bundle: Bundle): OptionsDialogFragment {
            val fragment = OptionsDialogFragment()
            fragment.arguments = bundle
            return fragment
        }

        fun newInstanceForProgram(view: View, item: ProgramHeaderViewModel): OptionsDialogFragment {
            val bundle = createBundle(view)
            bundle.putSerializable(HOLDER_TYPE_KEY, HolderType.PROGRAM_HEADER)
            bundle.putParcelable(ITEM_KEY, item)
            return newInstance(bundle)
        }

        fun newInstanceForTraining(
            view: View,
            holderType: HolderType,
            trainingType: TrainingType,
            item: TrainingViewModel
        ): OptionsDialogFragment {
            val bundle = createBundle(view)
            bundle.putSerializable(HOLDER_TYPE_KEY, holderType)
            bundle.putSerializable(TRANING_TYPE_KEY, trainingType)
            bundle.putParcelable(ITEM_KEY, item)
            return newInstance(bundle)
        }
    }
}