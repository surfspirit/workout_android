package ru.workout24.ui.lk_layer.edit_profile

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import ru.workout24.network.Api
import ru.workout24.ui.lk_layer.edit_profile.data.dto.ProfileErrorDto
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileViewModel
import ru.workout24.ui.lk_layer.edit_profile.mapper.ProfileMapper
import ru.workout24.ui.lk_layer.edit_profile.repository.UserRepository
import ru.workout24.utills.ImagePicker
import ru.workout24.utills.ImagePicker.uploadFile
import ru.workout24.utills.RxGenericErrorConsumer
import ru.workout24.utills.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import ru.workout24.features.SOCIAL_TYPE
import ru.workout24.network.CreatePushTokenBody
import ru.workout24.utills.Preferences
import java.io.File
import javax.inject.Inject

class VMEditProfile @Inject constructor(
    val api: Api,
    val userRepository: UserRepository,
    val preferences: Preferences
) :
    BaseViewModel(), ImagePicker.OnUploadImageListener {
    val userAvatar = MutableLiveData<String>()
    val userProfile = MutableLiveData<ProfileViewModel>()
    val errorConsumer = RxGenericErrorConsumer(ProfileErrorDto::class.java)

    fun getProfile() {
        compositeDisposable.add(
            userRepository.getUser().load().map(ProfileMapper(preferences)).subscribe({
                userAvatar.postValue(it.profileAvatar)
                userProfile.postValue(it)
            }, {
                it.printStackTrace()
            })
        )
    }

    fun saveField(id: String?, value: Any?/*, groupId: String? = null*/) {
        if (id != null && value != null) {
            var map = HashMap<String, Any>(0)
            //если это число, то отправляем число
            val number = value.toString().toIntOrNull()
            // если boolean то отправляем boolean
            map[id] = number ?: if (value is Boolean) value else value.toString()
//            if (groupId != null) {
//                val groupMap = HashMap<String, HashMap<String, Any>>()
//                groupMap[groupId] = map
//                //TODO: убрать повторяющийся код
//                compositeDisposable.add(
//                    api.patchAccount(groupMap)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .doOnError(errorConsumer)
//                        .subscribe({}, {})
//                )
//            }
            compositeDisposable.add(
                api.patchAccount(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(errorConsumer)
                    .subscribe({}, {})
            )
        }
    }

    fun addSocial(type: SOCIAL_TYPE, token: String, error: () -> Unit) {
        // vk, fb, google
        val social = when (type) {
            SOCIAL_TYPE.FB -> {
                "fb"
            }
            SOCIAL_TYPE.VK -> {
                "vk"
            }
            else -> {
                "google"
            }
        }
        compositeDisposable.add(
            api.sendSocial(
                social,
                CreatePushTokenBody(token)
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, {
                    error()
                })
        )
    }

    fun deleteSocial(type: SOCIAL_TYPE, error: () -> Unit) {
        // vk, fb, google
        val social = when (type) {
            SOCIAL_TYPE.FB -> {
                "fb"
            }
            SOCIAL_TYPE.VK -> {
                "vk"
            }
            else -> {
                "google"
            }
        }
        compositeDisposable.add(
            api.deleteSocial(
                social
            ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({},
                {
                    error()
                })
        )
    }

    override fun fileLoaded(file: File, fileMediaType: MediaType?) {
        compositeDisposable.add(
            uploadFile(api, file, fileMediaType, { url ->
                userAvatar.postValue(url)
                saveField("avatar_url", url)
            }, { throwable ->
                throwable.printStackTrace()
            })
        )
    }

    override fun bitmapLoaded(bitmap: Bitmap) {
    }

    override fun resizedBitmapLoaded(bitmap: Bitmap) {
    }
}