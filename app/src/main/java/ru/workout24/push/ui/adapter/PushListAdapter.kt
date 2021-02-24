package ru.workout24.push.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import org.jetbrains.anko.find
import ru.workout24.R
import ru.workout24.push.data.PushBodyDto
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeAdapter
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel

internal class PushListAdapter(val listener: OnPushRepeatListener) : AbstractTypeAdapter() {
    override fun createHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): AbstractTypeHolder<out AbstractTypeViewModel> {
        return PushHolder(inflater.inflate(R.layout.item_push_debug, parent, false), listener)
    }
}

internal interface OnPushRepeatListener {
    fun onPushRepeat(push: PushBodyDto)
}

internal class PushHolder(itemView: View, val listener: OnPushRepeatListener) :
    AbstractTypeHolder<PushViewModel>(itemView) {
    override fun bind(data: PushViewModel) {
        val title = itemView.find<TextView>(R.id.title)
        val message = itemView.find<TextView>(R.id.message)
        val body = itemView.find<TextView>(R.id.body)
        val repeat = itemView.find<Button>(R.id.repeat)
        title.text = data.push.messageTitle
        message.text = data.push.messageBody
        body.text = data.push.dataMessage.toString()
        repeat.setOnClickListener { listener.onPushRepeat(data.push) }
    }
}

internal class PushViewModel(val push: PushBodyDto) : AbstractTypeViewModel(11)