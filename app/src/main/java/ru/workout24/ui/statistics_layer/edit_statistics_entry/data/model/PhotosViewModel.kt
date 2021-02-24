package ru.workout24.ui.statistics_layer.edit_statistics_entry.data.model

import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel

const val PHOTOS_TYPE = 200

class PhotosViewModel(val urls: ArrayList<String>, id: String? = null) : AbstractTypeViewModel(PHOTOS_TYPE)