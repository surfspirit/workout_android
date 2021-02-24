package ru.workout24.ui.wg_bonuses.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import ru.workout24.R
import ru.workout24.ui.wg_bonuses.data.viewmodel.LeaderViewModel
import ru.workout24.utills.base.typed_holder_adapter.*

class LeadersAdapter(private val glide: RequestManager): AbstractTypedIdSelectPaginationAdapter() {
    override fun createHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): AbstractTypeHolder<out AbstractIdTypeViewModel> {
        return LeaderHolder(
            this,
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_leader,
                parent,
                false
            ),
            glide
        )
    }

    fun selectUserById(id: String, positionCallabck: (Int) -> Unit) {
        val position = currentList?.indexOf(currentList?.find { (it as LeaderViewModel).id == id }) ?: -1
        if (position != -1) {
            positionCallabck(position)
            selectId = id
            if (selectPosition != null) notifyItemChanged(selectPosition!!)
            notifyItemChanged(position)
            selectPosition = position
        }
    }

    fun goToUserProfile(id: String) {
        selectUserById(id){ position ->
            val data = currentList?.find { (it as LeaderViewModel).id == id } as? LeaderViewModel
            data?.let { onSelect(it.itemId, position, it) }
        }
    }
}


