package ru.workout24.ui.helpful_info.articles_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.workout24.R
import ru.workout24.ui.helpful_info.articles_list.data.model.ArticleViewModel
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeAdapter
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel

class ArticlesTitleAdapter(var type: TitlesType) : AbstractTypeAdapter() {
    private var listener: (ArticleViewModel) -> Unit = {}

    override fun createHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): AbstractTypeHolder<out AbstractTypeViewModel> = ArticleTitleHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_filter, parent, false
        ),
        type,
        listener
    )
    fun setOnArticleClickListener(listener: (ArticleViewModel) -> Unit) {
        this.listener = listener
    }
}

enum class TitlesType {
    LOCKED, UNLOCKED
}