package ru.workout24.ui.wg_bonuses_detail.adapter.holder

import android.view.View
import android.widget.TextView
import ru.workout24.ui.wg_bonuses_detail.data.viewmodel.DetailHeaderViewModel
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder

class HeaderHolder(val view: View): AbstractTypeHolder<DetailHeaderViewModel>(view) {
    override fun bind(data: DetailHeaderViewModel) {
        (view as TextView).text = data.title
    }
}