package ru.workout24.ui.article


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View

import ru.workout24.R
import ru.workout24.ui.helpful_info.articles_list.ArticlesType
import ru.workout24.ui.helpful_info.articles_list.data.model.ArticleViewModel
import ru.workout24.utills.base.BaseFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_article.*

/**
 * A simple [Fragment] subclass.
 */
class ArticleFragment : BaseFragment(R.layout.fragment_article) {
    private val type get() = arguments!!.getSerializable(ARTICLE_TYPE_KEY) as ArticlesType
    private val data get() = arguments!!.getParcelable<ArticleViewModel>(ARTICLE_KEY)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = when (type) {
            ArticlesType.ABOUT_SPORT -> resources.getString(R.string.about_sport)
            ArticlesType.ABOUT_FOOD -> resources.getString(R.string.about_food)
        }
        appBarLayout.setTitleText(title)
        appBarLayout.setOnBackClick {
            controller.popBackStack()
        }
        data.apply {
            article_title.text = title
            val data = "<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\"></head><body style=\"margin:0\">\n" +
                       text +
                       "</body></html>"
            article_text.isScrollbarFadingEnabled = true
            article_text.isVerticalScrollBarEnabled = false
            article_text.loadDataWithBaseURL("", data, "text/html", "UTF-8", "")
            if (!photoUrl.isNullOrEmpty()) {
                Glide.with(requireContext()).load(photoUrl).into(article_photo)
            }
        }
    }

    companion object {
        val ARTICLE_TYPE_KEY = "ARTICLE_TYPE_KEY"
        val ARTICLE_KEY = "ARTICLE_KEY"

        fun createBundle(data: ArticleViewModel, type: ArticlesType): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(ARTICLE_KEY, data)
            bundle.putSerializable(ARTICLE_TYPE_KEY, type)
            return bundle
        }
    }
}
