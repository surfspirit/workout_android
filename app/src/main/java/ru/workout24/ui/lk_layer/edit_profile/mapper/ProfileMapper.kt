package ru.workout24.ui.lk_layer.edit_profile.mapper

import ru.workout24.R
import ru.workout24.network.User
import ru.workout24.ui.lk_layer.edit_profile.data.model.*
import ru.workout24.utills.*
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel
import io.reactivex.functions.Function
import java.lang.Exception

class ProfileMapper(val preferences: Preferences) : Function<User, ProfileViewModel> {
    override fun apply(data: User): ProfileViewModel {
        return ProfileViewModel(
            data.avatar_url ?: "",
            getProfileItems(data)
        )
    }

    private fun setVkFbToPref(data: User) {
        data.vkId?.let { preferences.vk = it }
        data.fbId?.let { preferences.fb = it }
    }

    private fun getProfileItems(data: User): ArrayList<AbstractTypeViewModel> {
        return arrayListOf(
            ProfileTextViewModel(
                title = "Имя",
                value = data.name,
                hint = "Введите имя",
                id = "name"
            ),
            ProfileTextViewModel(
                title = "Фамилия",
                value = data.surname,
                hint = "Введите фамилию",
                id = "surname"
            ),
            ProfileDateViewModel(
                title = "Дата рождения",
                date = getBirthdayDate(data.bdate),
                id = "bdate"
            ),
            ProfileSelectEnumViewModel(
                title = "Пол",
                value = getGender(data.gender),
                keyMap = genders,
                variants = arrayListOf(GENDER.NONE, GENDER.MALE, GENDER.FEMALE),
                hint = "Выберите пол",
                id = "gender"
            ),
            ProfileRangeViewModel(
                title = "Рост",
                value = data.currentStats?.height ?: 0F,
                min = 10F,
                max = 210F,
                suffix = "см",
                hint = "Введите рост",
                id = "height",
                isLocked = true
//                groupId = "current_stats"
            ),
            ProfileRangeViewModel(
                title = "Вес",
                value = data.currentStats?.weight ?: 0F,
                min = 10F,
                max = 150F,
                suffix = "кг",
                hint = "Введите вес",
                id = "weight",
                isLocked = true
//                groupId = "current_stats"
            ),
            ProfileWhiteSpaceViewModel(),
            ProfileSelectEnumViewModel(
                title = "Уровень",
                value = if (data.trainingLevel!=null) TRAINING_LEVEL.valueOf(data.trainingLevel) else TRAINING_LEVEL.LEVEL_3,
                keyMap = levelsEnum,
                variants = arrayListOf(
                    TRAINING_LEVEL.LEVEL_1,
                    TRAINING_LEVEL.LEVEL_2,
                    TRAINING_LEVEL.LEVEL_3,
                    TRAINING_LEVEL.LEVEL_4,
                    TRAINING_LEVEL.LEVEL_5
                ),
                hint = "Выберите уровень",
                id = "training_level",
                isLocked = false
            ),
            ProfileSelectEnumViewModel(
                title = "Цель",
                value = getGoal(data.goals),
                keyMap = shortGoalsEnum,
                variants = arrayListOf(
                    GOAL.NONE,
                    GOAL.MUSCLE_BUILDING,
                    GOAL.HEALTHY_AND_STRONG,
                    GOAL.LOSE_WEIGHT
                ),
                hint = "Выберите цель",
                id = "goals"
            ),
            ProfileWhiteSpaceViewModel(),
            ProfileSwitchViewModel(
                title = "Профиль Facebook",
                isChecked = !data.fbId.isNullOrEmpty(),
                id = "fb_id"
            ),
            ProfileSwitchViewModel(
                title = "Профиль VK",
                isChecked = !data.vkId.isNullOrEmpty(),
                id = "vk_id"
            ),
//            ProfileSwitchViewModel(
//                title = "Apple Здоровье",
//                isChecked = false,
//                isLocked = true
//            ),
            ProfileWhiteSpaceViewModel(),
            ProfileSwitchViewModel(
                title = "Уведомления",
                isChecked = data.notifications ?: false,
                id = "notifications"
            ),
            ProfileWhiteSpaceViewModel(),
            ProfileArrowViewModel(
                title = "Сменить пароль",
                navigateTo = R.id.changePasswordFragment,
                canNavigate = true
            ),
            ProfileWhiteSpaceViewModel()
            )
    }

    private fun getBirthdayDate(date: String?): String {
        return try {
            transformStringDate(date)
        } catch (e: Exception) {
            ""
        }
    }

    private fun getGender(gender: String?) = gender?.let { GENDER.valueOf(gender) } ?: GENDER.NONE

    private fun getGoal(goal: String?) = goal?.let { GOAL.valueOf(goal) } ?: GOAL.HEALTHY_AND_STRONG
}