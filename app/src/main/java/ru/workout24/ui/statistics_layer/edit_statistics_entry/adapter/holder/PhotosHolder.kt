package ru.workout24.ui.statistics_layer.edit_statistics_entry.adapter.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.workout24.R
import ru.workout24.ui.statistics_layer.edit_statistics_entry.adapter.PhotoAdapter
import ru.workout24.ui.statistics_layer.edit_statistics_entry.data.model.PhotosViewModel
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder
import org.jetbrains.anko.find

class PhotosHolder(val bindingView: View, val photoAdapter: PhotoAdapter): AbstractTypeHolder<PhotosViewModel>(bindingView) {
    private val rvPhotos by lazy {
        val rvPhotos = bindingView.find<RecyclerView>(R.id.rv_photo)
        rvPhotos.adapter = photoAdapter
        rvPhotos
    }

    override fun bind(data: PhotosViewModel) {
        rvPhotos
        photoAdapter.setAdapterData(data.urls)
    }
}