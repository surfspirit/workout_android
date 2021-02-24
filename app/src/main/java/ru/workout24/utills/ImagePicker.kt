package ru.workout24.utills

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import ru.workout24.BuildConfig
import ru.workout24.network.Api
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.support.v4.toast
import java.io.ByteArrayOutputStream

import java.io.File
import java.io.FileNotFoundException
import java.util.ArrayList


object ImagePicker {
    private const val REQUEST_PERMISSION_IMAGE_CODE = 1122
    private const val PICK_IMAGE_CODE = 1133

    private const val READ_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private const val GRANTED = PackageManager.PERMISSION_GRANTED

    private val DEFAULT_MIN_WIDTH_QUALITY = 100        // min pixels
    private val TAG = "ImagePickerBefitter"
    private val TEMP_IMAGE_NAME = "befitterTempImage"
    private var TEMP_COUNTER: Long = System.currentTimeMillis()

    var minWidthQuality = DEFAULT_MIN_WIDTH_QUALITY

    fun pickImage(fragment: Fragment) {
        if (checkPermissions(fragment)) {
            fragment.startActivityForResult(
                getPickImageIntent(fragment.requireContext(), PickerSelect.GALLERY),
                PICK_IMAGE_CODE
            )
        }
    }

    fun createPhoto(fragment: Fragment) {
        if (checkPermissions(fragment)) {
            fragment.startActivityForResult(
                getPickImageIntent(fragment.requireContext(), PickerSelect.CAMERA),
                PICK_IMAGE_CODE
            )
        }
    }

    fun getPickedImage(
        context: Context,
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        listener: OnUploadImageListener
    ) {
        if (PICK_IMAGE_CODE == requestCode) {
            getImageFromResult(context, resultCode, data, { file, type ->
                listener.fileLoaded(file, type)
            }, { orginBitmap ->

            }, { resizingBitmap ->

            })
        }
    }

    private fun checkPermissions(fragment: Fragment): Boolean {
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
        fragment: Fragment,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        permissionDeniedCallback: () -> Unit
    ) {
        if (requestCode == REQUEST_PERMISSION_IMAGE_CODE) {
//            if (permissions.size == grantResults.size) {
//                pickImage(fragment)
//                return
//            }
            val readStoragePermissionIndex = permissions.indexOf(READ_STORAGE)
            if (readStoragePermissionIndex != -1 && grantResults[readStoragePermissionIndex] == GRANTED) {
                pickImage(fragment)
            } else {
                permissionDeniedCallback()
                fragment.toast("Не удалось получить разрешение для выбора фотографий")
            }
        }
    }

    private fun getPickImageIntent(context: Context, type: PickerSelect): Intent? {
        var chooserIntent: Intent? = null

        var intentList: MutableList<Intent> = ArrayList()

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
        imageReturnedIntent: Intent?
    ): Bitmap? {
        var bm: Bitmap? = null
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
            bm = getImageResized(context, selectedImage)
            val rotation = getRotation(context, selectedImage, isCamera)
            bm = rotate(bm, rotation)
        }
        return bm
    }

    private fun getImageFromResult(
        context: Context, resultCode: Int,
        imageReturnedIntent: Intent?,
        fileCallback: (File, MediaType?) -> Unit,
        bitmapCallback: (Bitmap) -> Unit,
        resizingBitmapCallback: (Bitmap?) -> Unit
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
            bm = getImageResized(context, selectedImage)
            val rotation = getRotation(context, selectedImage, isCamera)
            bm = rotate(bm, rotation)
            resizingBitmapCallback(bm)
        }
    }

    fun getImageUriFromResult(
        context: Context, resultCode: Int,
        imageReturnedIntent: Intent?
    ): Uri? {
        Log.d(TAG, "getImageFromResult, resultCode: $resultCode")
        //var bm: Bitmap? = null
        val imageFile = getTempFile(context)
        TEMP_COUNTER = System.currentTimeMillis()
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
            Log.d(TAG, "selectedImage: " + selectedImage!!)
            return selectedImage

            //bm = getImageResized(context, selectedImage)
            //val rotation = getRotation(context, selectedImage, isCamera)
            // bm = rotate(bm, rotation)
        }
        return null
    }


    private fun getTempFile(context: Context): File {
        val imageFile = File(context.externalCacheDir, TEMP_IMAGE_NAME + TEMP_COUNTER.toString())
        imageFile.parentFile.mkdirs()
        return imageFile
    }

    private fun decodeBitmap(context: Context, theUri: Uri, sampleSize: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inSampleSize = sampleSize

        var fileDescriptor: AssetFileDescriptor? = null
        try {
            fileDescriptor = context.contentResolver.openAssetFileDescriptor(theUri, "r")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        val actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
            fileDescriptor!!.fileDescriptor, null, options
        )

        Log.d(
            TAG, options.inSampleSize.toString() + " sample method bitmap ... " +
                    actuallyUsableBitmap.width + " " + actuallyUsableBitmap.height
        )

        return actuallyUsableBitmap
    }

    /**
     * Resize to avoid using too much memory loading big images (e.g.: 2560*1920)
     */
    private fun getImageResized(context: Context, selectedImage: Uri): Bitmap {
        var bm: Bitmap? = null
        val sampleSizes = intArrayOf(5, 3, 2, 1)
        var i = 0
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i])
            i++
        } while (bm!!.width < minWidthQuality && i < sampleSizes.size)
        return bm
    }


    private fun getRotation(context: Context, imageUri: Uri, isCamera: Boolean): Int {
        val rotation: Int
        if (isCamera) {
            rotation = getRotationFromCamera(context, imageUri)
        } else {
            rotation = getRotationFromGallery(context, imageUri)
        }
        Log.d(TAG, "Image rotation: $rotation")
        return rotation
    }

    private fun getRotationFromCamera(context: Context, imageFile: Uri): Int {
        var rotate = 0
        try {

            context.contentResolver.notifyChange(imageFile, null)
            val exif = ExifInterface(imageFile.path)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return rotate
    }

    private fun getRotationFromGallery(context: Context, imageUri: Uri): Int {
        var result = 0
        val columns = arrayOf(MediaStore.Images.Media.ORIENTATION)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(imageUri, columns, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val orientationColumnIndex = cursor.getColumnIndex(columns[0])
                result = cursor.getInt(orientationColumnIndex)
            }
        } catch (e: Exception) {
            //Do nothing
        } finally {
            cursor?.close()
        }//End of try-catch block
        return result
    }


    private fun rotate(bm: Bitmap?, rotation: Int): Bitmap? {
        if (rotation != 0) {
            val matrix = Matrix()
            matrix.postRotate(rotation.toFloat())
            return Bitmap.createBitmap(bm!!, 0, 0, bm.width, bm.height, matrix, true)
        }
        return bm
    }

    fun uploadFile(
        api: Api,
        file: File,
        fileMediaType: MediaType?,
        successCallback: (url: String) -> Unit,
        errorCallback: (throwable: Throwable) -> Unit
    ): Disposable {

        val requestFile = RequestBody.create(fileMediaType, file)

        val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)

        return api.uploadFile(multipartBody, requestFile)
            .doOnNext {
                successCallback(it.data!!.url)
            }
            .doOnError {
                errorCallback(it)
            }
            .doFinally {

            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe()
    }

    private fun Bitmap.toBase64String(): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun getFileMediaType(context: Context, fileUri: Uri): MediaType? {
        val type = context.contentResolver.getType(fileUri)
        return MediaType.parse(type ?: "*/*")
    }

    interface OnUploadImageListener {
        fun fileLoaded(file: File, fileMediaType: MediaType?)
        fun bitmapLoaded(bitmap: Bitmap)
        fun resizedBitmapLoaded(bitmap: Bitmap)
    }
}

enum class PickerSelect{
    GALLERY, CAMERA
}