package ru.workout24.ui.wg_bonuses_detail.adapter.holder

import android.view.View
import android.widget.TextView
import ru.workout24.R
import ru.workout24.ui.wg_bonuses_detail.BonusesProgressView
import ru.workout24.ui.wg_bonuses_detail.data.viewmodel.ProgressViewModel
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder
import org.jetbrains.anko.find

class ProgressLineHolder(val view: View): AbstractTypeHolder<ProgressViewModel>(view) {
    override fun bind(data: ProgressViewModel) {
        view.find<TextView>(R.id.plus_bonuses).text = "+${data.bonusesCount} WG бонус"
        view.find<BonusesProgressView>(R.id.progress_line).setProgressCount(data.bonusesCount, data.totalLevelBonuses)
        view.find<TextView>(R.id.bonuses_left).text = "+${data.bonusesLeft} WG бонус"
    }
}