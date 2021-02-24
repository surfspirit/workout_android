package ru.workout24.ui.statistics_layer.edit_statistics_entry.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.workout24.R
import ru.workout24.ui.statistics_layer.edit_statistics_entry.adapter.StatisticsEntryListener
import ru.workout24.utills.hide
import ru.workout24.utills.show
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.jetbrains.anko.find

class PhotoItemHolder(val bindingView: View, val listener: StatisticsEntryListener) :
    RecyclerView.ViewHolder(bindingView) {

    fun bind(url: String?, showAddPhotoBtn: Boolean) {
        val ivImage = bindingView.find<ImageView>(R.id.iv_save)
            val btnAddPhoto = bindingView.find<FloatingActionButton>(R.id.btn_add_photo)
            val ivRemove = bindingView.find<ImageView>(R.id.iv_remove)
            val txtEmptyPhotoText = bindingView.find<TextView>(R.id.empty_photo_text)

            if (url == null) {
                ivImage.setImageResource(0)
                ivRemove.hide()
                txtEmptyPhotoText.show()
            } else {
                Glide.with(bindingView.context).load(url).into(ivImage)
                ivRemove.show()
                txtEmptyPhotoText.hide()
            }

            if (showAddPhotoBtn)
                btnAddPhoto.show()
            else
                btnAddPhoto.hide()

            btnAddPhoto.setOnClickListener {
                listener.onPickImage(adapterPosition)
            }

            ivRemove.setOnClickListener {
                listener.onDeleteImage(adapterPosition)
            }
    }
}