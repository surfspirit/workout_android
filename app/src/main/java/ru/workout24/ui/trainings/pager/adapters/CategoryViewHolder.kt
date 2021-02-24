package ru.workout24.ui.trainings.pager.adapters

import android.view.View
import android.view.animation.RotateAnimation
import android.widget.TextView
import ru.workout24.ui.trainings.pager.pojos.Category
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder

import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import ru.workout24.R

class CategoryViewHolder(itemView: View) : GroupViewHolder(itemView) {

    private val genreName: TextView = itemView.findViewById(R.id.list_item_genre_name)
    private val arrow: ImageView = itemView.findViewById(R.id.list_item_genre_arrow) as ImageView

    fun setCategoryTitle(genre: ExpandableGroup<*>) {
        if (genre is Category) {
            genreName.text = genre.getTitle()
        }
        if (genre is MultiCheckCategory) {
            genreName.text = genre.getTitle()
        }
    }

    override fun expand() {
        animateExpand()
    }

    override fun collapse() {
        animateCollapse()
    }

    private fun animateExpand() {
        val rotate = RotateAnimation(360f, 450f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
        rotate.interpolator = LinearInterpolator()
        rotate.duration = 300
        rotate.fillAfter = true
        arrow.animation = rotate
    }

    private fun animateCollapse() {
        val rotate = RotateAnimation(450f, 360f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
        rotate.interpolator = LinearInterpolator()
        rotate.duration = 300
        rotate.fillAfter = true
        arrow.animation = rotate
    }
}