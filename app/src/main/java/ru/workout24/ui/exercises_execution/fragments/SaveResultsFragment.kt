package ru.workout24.ui.exercises_execution.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_save_results_training1.*
import ru.workout24.R
import ru.workout24.ui.exercises_execution.ExercisesExecutionViewModel
import ru.workout24.view.Photo
import ru.workout24.view.PhotoPicker
import ru.workout24.view.PhotosViewAdapter

class SaveResultsFragment : Fragment(R.layout.fragment_save_results_training1) {
    private val viewModel by lazy {
        ViewModelProviders.of(requireParentFragment())[ExercisesExecutionViewModel::class.java]
    }
    private val photosAdapter: PhotosViewAdapter = PhotosViewAdapter {
        photosPicker.pickImage()
    }
    private val photosPicker = PhotoPicker(this) { file, fileMediaType ->
        photosAdapter.setPhoto(Photo(file, fileMediaType))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.showProgressDialog.observe(viewLifecycleOwner, Observer {
            btn_go_to_statistics.isEnable = !it
            btn_go_to_statistics.showProgress(it)
        })

        tv_skip.setOnClickListener {
            viewModel.skipResultSaving()
        }

        btn_go_to_statistics.setOnClickListener {
            viewModel.savePhotos(photosAdapter.photos)
            viewModel.moveToEditStatisticFragment()
        }

        rv_photos.adapter = photosAdapter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        photosPicker.validateRequestedPermissions(requestCode, permissions, grantResults) {}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        photosPicker.getPickedImage(requireContext(), requestCode, resultCode, data)
    }
}