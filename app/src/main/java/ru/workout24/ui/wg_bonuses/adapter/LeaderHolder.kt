package ru.workout24.ui.wg_bonuses.adapter

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import ru.workout24.R
import ru.workout24.ui.wg_bonuses.data.viewmodel.LeaderViewModel
import ru.workout24.utills.base.typed_holder_adapter.SelectableTypeHolder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.mikhaellopez.circularimageview.CircularImageView
import org.jetbrains.anko.find
import ru.workout24.utills.base.typed_holder_adapter.IdSelectListener

class LeaderHolder(private val selectListener: IdSelectListener, private val bindingView: View, private val glide: RequestManager) :
    SelectableTypeHolder<LeaderViewModel>(itemView = bindingView) {

    private val bgGray = ContextCompat.getColor(bindingView.context, R.color.selectGray)
    private val bgWhite = ContextCompat.getColor(bindingView.context, android.R.color.white)
    private val blackColor = ContextCompat.getColor(bindingView.context, R.color.black)
    private val whiteFiveColor = ContextCompat.getColor(bindingView.context, R.color.whiteFive)
    private val warmGrayColor = ContextCompat.getColor(bindingView.context, R.color.warmGray)

    private val avatar by lazy {
        bindingView.find<CircularImageView>(R.id.avatar)
    }

    private val name by lazy {
        bindingView.find<TextView>(R.id.name)
    }

    private val bonuses by lazy {
        bindingView.find<TextView>(R.id.bonuses)
    }

    private val position by lazy {
        bindingView.find<TextView>(R.id.position)
    }

    override fun bind(data: LeaderViewModel) {
        name.text = data.name
        bonuses.text = data.wgBonuses.toString()
        position.text = data.position.toString()
        if (isSelected) {
            bindingView.setBackgroundColor(bgGray)
            name.setTextColor(whiteFiveColor)
            bonuses.setTextColor(whiteFiveColor)
        } else {
            bindingView.setBackgroundColor(bgWhite)
            name.setTextColor(warmGrayColor)
            bonuses.setTextColor(blackColor)
        }
        avatar.setImageDrawable(null)
        glide
            .load(data.avatarUrl)
            .apply(RequestOptions().placeholder(R.drawable.avatar_placeholder).error(R.drawable.avatar_placeholder))
            .into(avatar)
        bindingView.setOnClickListener {
            selectListener.onSelect(data.itemId, adapterPosition, data)
        }
    }
}