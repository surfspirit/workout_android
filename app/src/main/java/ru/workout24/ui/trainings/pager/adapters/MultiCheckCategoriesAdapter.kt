package ru.workout24.ui.trainings.pager.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import ru.workout24.R
import ru.workout24.ui.trainings.pager.pojos.Criteria
import com.thoughtbot.expandablecheckrecyclerview.CheckableChildRecyclerViewAdapter
import com.thoughtbot.expandablecheckrecyclerview.listeners.OnCheckChildClickListener
import com.thoughtbot.expandablecheckrecyclerview.models.CheckedExpandableGroup
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class MultiCheckCategoriesAdapter(groups: List<MultiCheckCategory>) :
    CheckableChildRecyclerViewAdapter<CategoryViewHolder, MultiCheckCriteriaViewHolder>(groups) {
    var clickListener: OnCheckChildClickListener? = null
    override fun onCreateCheckChildViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MultiCheckCriteriaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_multicheck_criteria, parent, false)
        return MultiCheckCriteriaViewHolder(view)
    }

    override fun onBindCheckChildViewHolder(
        holder: MultiCheckCriteriaViewHolder, position: Int,
        group: CheckedExpandableGroup, childIndex: Int
    ) {
        val artist = group.items[childIndex] as Criteria?
        holder.setArtistName(artist?.name)
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_filter, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindGroupViewHolder(
        holder: CategoryViewHolder, flatPosition: Int,
        group: ExpandableGroup<*>
    ) {
        holder.setCategoryTitle(group)
    }

    override fun setChildClickListener(listener: OnCheckChildClickListener?) {
        this.clickListener = listener
        super.setChildClickListener(listener)
    }

    override fun checkChild(checked: Boolean, groupIndex: Int, childIndex: Int) {
        super.setChildClickListener(null)
        super.checkChild(checked, groupIndex, childIndex)
        super.setChildClickListener(clickListener)
    }

    fun resetAll() {
        groups.forEachIndexed { grouupIndex, group ->
            group.items.forEachIndexed { childIndex, _ ->
                checkChild(false, grouupIndex, childIndex)
            }
        }
    }
}