package ru.workout24.ui.trainings.pager.adapters


import ru.workout24.ui.trainings.pager.pojos.Criteria
import com.thoughtbot.expandablecheckrecyclerview.models.MultiCheckExpandableGroup

class MultiCheckCategory(
    title: String,
    val serverName: String,
    items: List<Criteria>
) : MultiCheckExpandableGroup(title, items)