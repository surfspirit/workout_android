package ru.workout24.ui.wg_bonuses_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.workout24.R
import ru.workout24.ui.wg_bonuses_detail.adapter.holder.DetailsHolder
import ru.workout24.ui.wg_bonuses_detail.adapter.holder.HeaderHolder
import ru.workout24.ui.wg_bonuses_detail.adapter.holder.OnItemListener
import ru.workout24.ui.wg_bonuses_detail.adapter.holder.ProgressLineHolder
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeAdapter
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel
import java.lang.IllegalArgumentException

class WgBonusesDetailAdapter : AbstractTypeAdapter() {
    private var positionClickListener: OnItemListener? = null

    override fun createHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): AbstractTypeHolder<out AbstractTypeViewModel> {
        return when (viewType) {
            DetailType.HEADER.value -> HeaderHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_deatil_header,
                    parent,
                    false
                )
            )
            DetailType.DETAIL.value -> DetailsHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_detail,
                    parent,
                    false
                ), false
            )
            DetailType.CLICKABLE_DETAIL.value -> DetailsHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_detail,
                    parent,
                    false
                ), true, positionClickListener
            )
            DetailType.PROGRESS_LINE.value -> ProgressLineHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_progress_line,
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Отсутствует holder c типом $viewType")
        }
    }

    fun setOnPositionClickListener(listener: OnItemListener) {
        positionClickListener = listener
    }
}

enum class DetailType(val value: Int) {
    HEADER(0), DETAIL(1), CLICKABLE_DETAIL(2), PROGRESS_LINE(3)
}