package ru.workout24.ui.helpful_info.articles_list.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ru.workout24.R
import ru.workout24.ui.helpful_info.articles_list.data.model.ArticleViewModel
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder
import ru.workout24.utills.hide
import ru.workout24.utills.show
import org.jetbrains.anko.find

class ArticleTitleHolder(val view: View, val type: TitlesType, val onArticleClick: (ArticleViewModel) -> Unit) :
    AbstractTypeHolder<ArticleViewModel>(view) {
    override fun bind(data: ArticleViewModel) {
        val title = view.find<TextView>(R.id.list_item_genre_name)
        val icon_unlocked = view.find<ImageView>(R.id.list_item_genre_arrow)
        val icon_locked = view.find<ImageView>(R.id.list_item_genre_lock)
        title.text = data.title
        when(type) {
            TitlesType.LOCKED -> {
                icon_locked.show()
                icon_unlocked.hide()
            }
            TitlesType.UNLOCKED -> {
                icon_unlocked.show()
                icon_locked.hide()
            }
        }
        view.setOnClickListener { onArticleClick(data) }
    }
}