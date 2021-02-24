package ru.workout24.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.MediaType
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.toast
import ru.workout24.BuildConfig
import ru.workout24.R
import ru.workout24.utills.PathUtil
import ru.workout24.utills.hide
import ru.workout24.utills.show
import java.io.File

class PhotosView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) :
    RecyclerView(context, attributeSet) {

    init {
        layoutManager = GridLayoutManager(context, 3)
        overScrollMode = View.OVER_SCROLL_NEVER
    }

}

class PhotosViewAdapter(private val pickImageAction: () -> Unit) :
    RecyclerView.Adapter<PhotosViewHolder>() {
    var photos = ArrayList<Photo>(0)
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val listener = object : PhotosAdapterListener {
        override fun onPickPhoto() {
            pickImageAction()
        }

        override fun onDeletePhoto(position: Int) {
            photos.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        return PhotosViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_photo,
                parent,
                false
            ), listener
        )
    }

    override fun getItemCount(): Int = 3

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.bindPhoto(photos.getOrNull(position), position == 1 && photos.size != 3)
    }

    fun setPhoto(photo: Photo) {
        photos.add(photo)
        notifyDataSetChanged()
    }
}

interface PhotosAdapterListener {
    fun onPickPhoto()
    fun onDeletePhoto(position: Int)
}

class PhotosViewHolder(private val bindingView: View, private val listener: PhotosAdapterListener) :
    RecyclerView.ViewHolder(bindingView) {
    fun bindPhoto(photo: Photo?, isShowAddBtn: Boolean) {
        val ivImage = bindingView.find<ImageView>(R.id.iv_save)
        val btnAddPhoto = bindingView.find<FloatingActionButton>(R.id.btn_add_photo)
        val ivRemove = bindingView.find<ImageView>(R.id.iv_remove)

        if (photo == null) {
            ivImage.setImageResource(0)
            ivRemove.hide()
        } else {
            ivImage.setImageURI(photo.bitmapUri)
            ivRemove.show()
        }

        if (isShowAddBtn)
            btnAddPhoto.show()
        else
            btnAddPhoto.hide()

        btnAddPhoto.setOnClickListener {
            listener.onPickPhoto()
        }

        photo?.let {
            ivRemove.setOnClickListener {
                listener.onDeletePhoto(adapterPosition)
            }
        }
    }
}

class PhotoPicker(private val fragment: Fragment, private val filePickedAction: (file: File, fileMediaType: MediaType?) -> Unit) {
    private val REQUEST_PERMISSION_IMAGE_CODE = 1122
    private val PICK_IMAGE_CODE = 1133

    private val READ_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private val GRANTED = PackageManager.PERMISSION_GRANTED

    private val TAG = "ImagePickerBefitter"
    private val TEMP_IMAGE_NAME = "befitterTempImage"
    private var TEMP_COUNTER: Long = System.currentTimeMillis()

    fun pickImage() {
        if (checkPermissions()) {
            fragment.startActivityForResult(
                getPickImageIntent(fragment.requireContext()),
                PICK_IMAGE_CODE
            )
        }
    }

    fun getPickedImage(
        context: Context,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (PICK_IMAGE_CODE == requestCode) {
            getImageFromResult(context, resultCode, data) { file, type ->
                filePickedAction(file, type)
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val storageReadPermission = ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            READ_STORAGE
        )
        if (storageReadPermission != GRANTED) {
            fragment.requestPermissions(
                arrayOf(READ_STORAGE),
                REQUEST_PERMISSION_IMAGE_CODE
            )
            return false
        } else {
            return true
        }
    }

    fun validateRequestedPermissions(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        permissionDeniedCallback: () -> Unit
    ) {
        if (requestCode == REQUEST_PERMISSION_IMAGE_CODE) {
            val readStoragePermissionIndex = permissions.indexOf(READ_STORAGE)
            if (readStoragePermissionIndex != -1 && grantResults[readStoragePermissionIndex] == GRANTED) {
                pickImage()
            } else {
                permissionDeniedCallback()
                fragment.toast("Не удалось получить разрешение для выбора фотографий")
            }
        }
    }

    private fun getPickImageIntent(context: Context): Intent? {
        var chooserIntent: Intent? = null

        var intentList: MutableList<Intent> = java.util.ArrayList()

        val pickIntent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhotoIntent.putExtra("return-data", true)
        takePhotoIntent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".provider",
                getTempFile(context)
            )
        )
        intentList = addIntentsToList(context, intentList, pickIntent)
        intentList = addIntentsToList(context, intentList, takePhotoIntent)

        if (intentList.size > 0) {
            chooserIntent = Intent.createChooser(
                intentList.removeAt(intentList.size - 1),
                "Выберите фотографию"
            )
            chooserIntent!!.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                intentList.toTypedArray<Parcelable>()
            )
        }

        return chooserIntent
    }

    private fun addIntentsToList(
        context: Context,
        list: MutableList<Intent>,
        intent: Intent
    ): MutableList<Intent> {
        val resInfo = context.packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in resInfo) {
            val packageName = resolveInfo.activityInfo.packageName
            val targetedIntent = Intent(intent)
            targetedIntent.setPackage(packageName)
            list.add(targetedIntent)
            Log.d(TAG, "Intent: " + intent.action + " package: " + packageName)
        }
        return list
    }

    private fun getImageFromResult(
        context: Context, resultCode: Int,
        imageReturnedIntent: Intent?,
        fileCallback: (File, MediaType?) -> Unit
    ) {
        var bm: Bitmap?
        val imageFile = getTempFile(context)
        if (resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri?
            val isCamera = imageReturnedIntent == null ||
                    imageReturnedIntent.data == null ||
                    imageReturnedIntent.data!!.toString().contains(imageFile.toString())
            if (isCamera) {
                /** CAMERA  */
                selectedImage = Uri.fromFile(imageFile)
            } else {
                /** ALBUM  */
                selectedImage = imageReturnedIntent!!.data
            }
            fileCallback(File(PathUtil.getRealPath(context, selectedImage)), getFileMediaType(context, selectedImage))
        }
    }

    private fun getTempFile(context: Context): File {
        val imageFile = File(context.externalCacheDir, TEMP_IMAGE_NAME + TEMP_COUNTER.toString())
        imageFile.parentFile.mkdirs()
        return imageFile
    }

    private fun getFileMediaType(context: Context, fileUri: Uri): MediaType? {
        val type = context.contentResolver.getType(fileUri)
        return MediaType.parse(type ?: "*/*")
    }
}

data class Photo(
    val file: File,
    val type: MediaType?
) {
    val bitmapUri: Uri get() = file.toUri()
}