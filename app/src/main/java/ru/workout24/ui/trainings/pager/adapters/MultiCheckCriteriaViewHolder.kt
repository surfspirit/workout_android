package ru.workout24.ui.trainings.pager.adapters


import android.view.View
import android.widget.Checkable
import android.widget.CheckedTextView
import ru.workout24.R
import com.thoughtbot.expandablecheckrecyclerview.viewholders.CheckableChildViewHolder

class MultiCheckCriteriaViewHolder(itemView: View) : CheckableChildViewHolder(itemView) {

    private val childCheckedTextView: CheckedTextView = itemView.findViewById(R.id.list_item_multicheck_artist_name) as CheckedTextView

    override fun getCheckable(): Checkable {
        return childCheckedTextView
    }

    fun setArtistName(artistName: String?) {
        childCheckedTextView.text = artistName
    }
}