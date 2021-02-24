package ru.workout24.ui.lk_layer.edit_profile


import android.os.Bundle
import android.view.View
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.lifecycle.Observer
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import ru.workout24.R
import ru.workout24.ui.lk_layer.edit_profile.adapter.ProfileItemAdapter
import ru.workout24.ui.lk_layer.edit_profile.adapter.ProfileItemsListener
import ru.workout24.ui.lk_layer.edit_profile.data.model.*
import ru.workout24.ui.lk_layer.edit_profile.utils.Dialogs
import ru.workout24.utills.*
import ru.workout24.utills.base.BaseFragment
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import ru.workout24.features.SOCIAL_TYPE
import ru.workout24.features.SocialLoginFeature
import javax.inject.Inject

class EditProfileFragment : BaseFragment(R.layout.fragment_edit_profile), ProfileItemsListener {
    @Inject
    lateinit var socialFeature: SocialLoginFeature

    private val adapter by lazy {
        ProfileItemAdapter(this)
    }
    private val viewModel by lazy {
        attachViewModel<VMEditProfile>(VMEditProfile::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout.setOnBackClick { controller.popBackStack() }
        rv_edit.adapter = adapter
        socialFeature.needLogin = false
        // TODO: раскоментировать когда будет готовы декораторы
//        rv_edit.addItemDecoration(
//            TypesDivider(
//                intArrayOf(
//                    ITEM_TYPE.TEXT.value,
//                    ITEM_TYPE.SWITCH.value,
//                    ITEM_TYPE.SELECT.value,
//                    ITEM_TYPE.NUMBER.value,
//                    ITEM_TYPE.DATE.value
//                ),
//                resources.getDimensionPixelSize(R.dimen.divider_height),
//                ContextCompat.getColor(requireContext(), R.color.whiteThree),
//                requireContext().resources.getDimensionPixelSize(R.dimen.divider_left_margin),
//                0,
//                Color.WHITE
//            )
//        )
//        rv_edit.addItemDecoration(
//            PositionsDivider(
//                intArrayOf(5),
//                0,
//                Color.TRANSPARENT,
//                0,
//                0,
//                Color.TRANSPARENT,
//                0,
//                32
//            )
//        )
        viewModel.getProfile()
        viewModel.userAvatar.observe(this, Observer { profileAvatar ->
            if (profileAvatar.isNotEmpty()) {
                glide.load(profileAvatar)
                    .apply(RequestOptions().placeholder(R.drawable.avatar_placeholder).error(R.drawable.avatar_placeholder))
                    .listener(object :
                        RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            //todo
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBlack.hide()
                            return false;
                        }
                    }
                    )
                    .into(profile_img)
            }
        })
        viewModel.userProfile.observe(this, androidx.lifecycle.Observer { profile ->
            adapter.setData(profile.profileItems)
        })
        viewModel.errorConsumer.observe(this) { error ->
            error.errors?.notifications?.let { message ->
                showInfoAlert(requireContext(), "", message, "Повторить", {}).showIfNeeded()
            }
        }
        btn_add_photo.setOnClickListener {
            ImagePicker.pickImage(this)
        }
    }

    override fun onItemClick(data: ProfileTextViewModel, position: Int) {
        when (data) {
            is ProfileSelectEnumViewModel<*> -> {
                if (!data.isLocked) {
                    Dialogs.showWheelInputDialog(
                        requireContext(),
                        data
                    ) {
                        viewModel.saveField(data.id, data.getEnumType().toString())
                        adapter.notifyItemChanged(position)
                    }
                }
            }
            is ProfileDateViewModel -> Dialogs.showDatePickerDialog(requireContext(), data) {
                viewModel.saveField(data.id, transformStringDateRevers(data.value.toString()))
                adapter.notifyItemChanged(position)
            }
            else -> Dialogs.showTextInputDialog(requireContext(), data) {
                viewModel.saveField(data.id, data.value)
                adapter.notifyItemChanged(position)
            }
        }
    }

    override fun onSwitchChange(switch: SwitchButton, data: ProfileSwitchViewModel) {
        if (data.id == "fb_id" || data.id == "vk_id" || data.id == "google_id") {
            val type = when (data.id) {
                "fb_id" -> SOCIAL_TYPE.FB
                "vk_id" -> SOCIAL_TYPE.VK
                else -> SOCIAL_TYPE.GOOGLE
            }
            if (data.isChecked) {
//                val hasInPref = when(type){
//                    SOCIAL_TYPE.FB -> !pref.fb.isNullOrEmpty()
//                        SOCIAL_TYPE.VK -> !pref.vk.isNullOrEmpty()
//                        SOCIAL_TYPE.GOOGLE -> false
//                }
//                if(hasInPref){
//                    val token = when(type){
//                        SOCIAL_TYPE.FB -> pref.fb
//                        SOCIAL_TYPE.VK -> pref.vk
//                        SOCIAL_TYPE.GOOGLE -> ""
//                    }
//                    viewModel.addSocial(type, token!!)
//                }else{
//                    socialFeature.createSocialLogin(this,type)
//                }
                socialFeature.createSocialLogin(this, type)
                socialFeature.setTokenCallback { token, socialType ->
                    viewModel.addSocial(socialType, token) {
                        switch.isChecked = false
                    }
                }
                socialFeature.setErrorTokenCallback { s, socialType ->
                    switch.isChecked = false
                }
            } else {
                viewModel.deleteSocial(type){
                    switch.isChecked = false
                }
            }
        } else {
            viewModel.saveField(data.id, data.isChecked)
        }
    }

    override fun onNavigateClick(data: ProfileArrowViewModel) {
        controller.navigate(data.navigateTo)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ImagePicker.validateRequestedPermissions(this, requestCode, permissions, grantResults) {
            //            Toast.makeText(requireContext(), "")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null)
            progressBlack.show()
        ImagePicker.getPickedImage(requireContext(), requestCode, resultCode, data, viewModel)
    }
}
