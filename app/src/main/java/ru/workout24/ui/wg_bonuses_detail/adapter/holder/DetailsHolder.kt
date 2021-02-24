package ru.workout24.ui.wg_bonuses_detail.adapter.holder

import android.view.View
import android.widget.TextView
import ru.workout24.R
import ru.workout24.ui.wg_bonuses_detail.data.viewmodel.DetailViewModel
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder
import org.jetbrains.anko.find

class DetailsHolder(val view: View, val clickable: Boolean, val listener: OnItemListener? = null): AbstractTypeHolder<DetailViewModel>(view) {
    override fun bind(data: DetailViewModel) {
        view.find<TextView>(R.id.title).text = data.title
        view.find<TextView>(R.id.value).text = data.value
        if (clickable) view.setOnClickListener { listener?.onClick() }
    }
}

interface OnItemListener {
    fun onClick()
}