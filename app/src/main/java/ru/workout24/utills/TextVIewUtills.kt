package ru.workout24.utills

import android.text.Html
import android.widget.TextView

fun TextView.setHtmlText(text: String) {
    setText(Html.fromHtml(text))
}