package ru.workout24.ui.lk_layer.personal_cab

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import ru.workout24.R
import ru.workout24.room.AppDatabase
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.levels
import ru.workout24.utills.visible
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_personal_cabinet.*
import org.jetbrains.anko.find
import ru.workout24.features.SocialLoginFeature
import ru.workout24.network.Api
import ru.workout24.push.DeepLinkStore
import ru.workout24.utills.sendFirebaseException
import javax.inject.Inject

class PersonalCabinetFragment : BaseFragment(R.layout.fragment_personal_cabinet, true) {
    @Inject
    lateinit var api: Api
    @Inject
    lateinit var db: AppDatabase
    @Inject
    lateinit var socialLoginFeature: SocialLoginFeature

    private val adapter: PersonalCabinetAdapter by lazy {
        attachAdapter(
            PersonalCabinetAdapter(
                context!!
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val drawer = activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
        drawer?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT)
        iv_menu.setOnClickListener {
            drawer?.openDrawer(GravityCompat.START)
        }

        iv_edit_profile.setOnClickListener {
            controller!!.navigate(R.id.action_personalCabinetFragment_to_editProfileFragment)
        }

        cl_sub.setOnClickListener {
            controller!!.navigate(
                R.id.action_personalCabinetFragment_to_globalChooseSubscriptionFragmentLk,
                bundleOf("show_back_button" to true)
            )
        }

        tv_delete.setOnClickListener {
            compositeDisposable.add(
                Completable.fromAction {
                    pref.clearAll()
                    db.clearAllTables()
                }.startWith(api.deleteAccount())/*.startWith(api.removeDevice(pref.pushToken!!))*/.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        // удаляем токен после того как удалим аккаунт
                        pref.authToken = ""
                        pref.pushToken =""
                        controller.navigate(
                            R.id.action_global_enterFragment
                        )
                    }, {
                        controller.navigate(
                            R.id.action_global_enterFragment
                        )
                        it.sendFirebaseException()
                    })
            )
        }

        tv_exit.setOnClickListener {
            showActionAlert(context!!, "Выход", "Вы уверены, что хотите выйти?", "Да", "Нет", {
                exit()
            }).show()
        }

        personal_cab_menu_list.adapter = adapter

        adapter.data = arrayListOf(
            Pair<String, View.OnClickListener>(
                first = "Статистика",
                second = View.OnClickListener {
                    controller!!.navigate(R.id.statistics_layer, bundleOf("from_lk" to true))
                }
            ),
            Pair<String, View.OnClickListener>(
                first = "Дневник тренировок",
                second = View.OnClickListener {
                    controller.navigate(R.id.action_personalCabinetFragment_to_workout_diary)
                }
            ),
            Pair<String, View.OnClickListener>(
                first = "WG бонусы",
                second = View.OnClickListener {
                    controller.navigate(R.id.action_personalCabinetFragment_to_wg_bonuses_fragment)
                }
            )
        )
        adapter.notifyDataSetChanged()

        resourceFactory.userResource.onChange(this, { user ->
            if (user.avatar_url != null)
                glide.load(user.avatar_url).into(profile_img)
            else
                glide.load(R.drawable.avatar_placeholder).into(profile_img)
            tv_name.text = user.name + " " + user.surname
            if (user.trainingLevel == "LEVEL_3")
                tv_level.text = "Средний уровень"
            else
                tv_level.text = levels[user.trainingLevel]

            cl_sub.visible(!user.have_subscription)
        })

        if (DeepLinkStore.hasData) {
            controller.navigate(DeepLinkStore.getObject().value)
        }
    }

    private fun exit() {
        compositeDisposable.add(
            Completable.fromAction {
                pref.clearAll()
                db.clearAllTables()
                socialLoginFeature.logout()
            }.startWith(api.removeDevice(pref.pushToken!!)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // удаляем токен после того как удалим аккаунт
                    pref.authToken = ""
                    pref.pushToken = ""
                    controller.navigate(
                        R.id.action_global_enterFragment
                    )
                }, {
                    controller.navigate(
                        R.id.action_global_enterFragment
                    )
                    it.sendFirebaseException()
                })
        )
    }

    class PersonalCabinetAdapter(val context: Context) : BaseAdapter(R.layout.item_pers_cab_menu) {
        init {


            onBind { holder, pos ->
                val personalCabMenuItem = holder.find<ConstraintLayout>(R.id.personal_cab_menu_item)
                val txtMenuItemText = holder.find<TextView>(R.id.txt_menu_item_text)

                //val list = data as ArrayList<Any>
                val item = (data[pos]) as Pair<String, View.OnClickListener>

                txtMenuItemText.text = item.first
                personalCabMenuItem.setOnClickListener(item.second)


            }


        }

    }

    override fun onResume() {
        super.onResume()
        resourceFactory.userResource.load()
    }
}