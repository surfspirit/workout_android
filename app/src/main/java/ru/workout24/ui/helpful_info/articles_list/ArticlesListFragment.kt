package ru.workout24.ui.helpful_info.articles_list


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

import ru.workout24.R
import ru.workout24.ui.article.ArticleFragment
import ru.workout24.ui.helpful_info.articles_list.adapter.ArticlesTitleAdapter
import ru.workout24.ui.helpful_info.articles_list.adapter.TitlesType
import ru.workout24.ui.helpful_info.articles_list.data.model.ArticleViewModel
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.custom_views.SimpleDivider
import ru.workout24.utills.toArrayList
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ArticlesListFragment : BaseFragment(R.layout.fragment_articles_list) {
    private val type get() = arguments!!.getSerializable(TYPE_KEY) as ArticlesType

    var data:ArrayList<ArticleViewModel> = arrayListOf()
    lateinit var adapter:ArticlesTitleAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view as RecyclerView
        view.addItemDecoration(SimpleDivider(
            resources.getDimensionPixelSize(R.dimen.article_list_divider_height),
            Color.TRANSPARENT
        ))
        adapter = ArticlesTitleAdapter(TitlesType.UNLOCKED)
        resourceFactory.userResource.load();
        resourceFactory.userResource.onChange(this, {user ->
            if (!user.have_subscription) {
                adapter = ArticlesTitleAdapter(TitlesType.LOCKED)
                adapter.setOnArticleClickListener {
                }

            } else {
                adapter = ArticlesTitleAdapter(TitlesType.UNLOCKED)
                adapter.setOnArticleClickListener {
                    controller.navigate(
                        R.id.action_helpfulinfo_to_article,
                        ArticleFragment.createBundle(it, type)
                    )
                }
            }
            view.adapter = adapter
            adapter.setData(data)
        }, {_ ->
            showInfoAlert(context!!, "Ошибка при выполнение запроса", "Произошла ошибка при выполнении запроса к серверу", "Ок", {});
        })

        val categories = resourceFactory.categories
        categories.loadIfNeeded()
        categories.onChange(this, {categories ->
            if (categories.size>=2) {
                var i = 0
                if (type == ArticlesType.ABOUT_SPORT)
                    i = 0
                else
                    i = 1
                data = categories[i].articles.toArrayList()
                adapter.setData(data)
            }
        }, {_ ->
            showInfoAlert(context!!, "Ошибка при выполнение запроса", "Произошла ошибка при выполнении запроса к серверу", "Ок", {});
        })

        data = arrayListOf()
        adapter.setData(data)

    }

    companion object {
        val TYPE_KEY = "TYPE_KEY"

        fun newInstance(type: ArticlesType): ArticlesListFragment {
            val bundle = Bundle()
            bundle.putSerializable(TYPE_KEY, type)
            val fragment = ArticlesListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}

enum class ArticlesType {
    ABOUT_SPORT, ABOUT_FOOD
}
