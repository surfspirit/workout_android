package ru.workout24.utills

import com.google.gson.annotations.SerializedName

const val NONE_INT = -1
const val NONE_STRING = "NONE"

enum class LANGUAGE {
    RU, EN
}

enum class ROLE {
    ADMIN, USER
}

enum class GENDER {
    NONE,
    MALE,
    FEMALE
}

enum class GO_TO_TRAINING {
    FROM_SET, FROM_ONCE_TRAININGS, FROM_SINGLE_EX
}

enum class DIFFICULTY(val value: String) {
    VERY_SIMPLE("VERY_SIMPLE"),
    SIMPLE("SIMPLE"),
    NORMAL("NORMAL"),
    HARD("HARD"),
    VERY_HARD("VERY_HARD")
}

enum class TRAINING_LEVEL(val value: String) {
    NONE("NONE"),
    LEVEL_1("LEVEL_1"), LEVEL_2("LEVEL_2"), LEVEL_3("LEVEL_3"), LEVEL_4("LEVEL_4"), LEVEL_5("LEVEL_5")
}

enum class GOAL(val value: String) {
    NONE("NONE"),
    MUSCLE_BUILDING("MUSCLE_BUILDING"),
    HEALTHY_AND_STRONG("HEALTHY_AND_STRONG"), LOSE_WEIGHT("LOSE_WEIGHT"),
}

enum class TARGET_TYPE(val value: String) {
    NONE("NONE"),
    TRAINING("TRAINING"), TRAINING_RESULT("TRAINING_RESULT"), TEST_EXERCISE("TEST_EXERCISE"), TEST_EXERCISE_RESULTS(
        "TEST_EXERCISE_RESULTS"
    ),
    SINGLE_EXERCISE("SINGLE_EXERCISE"), SINGLE_EXERCISE_RESULTS("SINGLE_EXERCISE_RESULTS")
}

val levels = mapOf(
    "LEVEL_1" to "Низкий",
    "LEVEL_2" to "Начинающий",
    "LEVEL_3" to "Средний",
    "LEVEL_4" to "Продвинутый",
    "LEVEL_5" to "Мастер"
)
val goals = mapOf(
    "MUSCLE_BUILDING" to "Хочу подкачаться, увеличить мышечную массу",
    "HEALTHY_AND_STRONG" to "Хочу поддержать здоровье и тонус",
    "LOSE_WEIGHT" to "Хочу похудеть, снизить количество жира"
)
val shortGoals = mapOf(
    "MUSCLE_BUILDING" to "Увеличить мышечную массу",
    "HEALTHY_AND_STRONG" to "Поддержать здоровье",
    "LOSE_WEIGHT" to "Снизить вес"
)
val levelsEnum = mapOf(
    TRAINING_LEVEL.LEVEL_1 to "Низкий",
    TRAINING_LEVEL.LEVEL_2 to "Начинающий",
    TRAINING_LEVEL.LEVEL_3 to "Средний",
    TRAINING_LEVEL.LEVEL_4 to "Продвинутый",
    TRAINING_LEVEL.LEVEL_5 to "Мастер"
)
val shortGoalsEnum = mapOf(
    GOAL.NONE to "Отсутствует",
    GOAL.MUSCLE_BUILDING to "Увеличить мышечную массу",
    GOAL.HEALTHY_AND_STRONG to "Поддержать здоровье",
    GOAL.LOSE_WEIGHT to "Снизить вес"
)
val genders = mapOf(
    GENDER.NONE to "Не указан",
    GENDER.MALE to "Мужской",
    GENDER.FEMALE to "Женский"
)
val difficultyEnum = mapOf(
    DIFFICULTY.VERY_SIMPLE to "Очень легко",
    DIFFICULTY.SIMPLE to "Легко",
    DIFFICULTY.NORMAL to "Нормально",
    DIFFICULTY.HARD to "Сложно",
    DIFFICULTY.VERY_HARD to "Очень сложно"
)
const val AUTH_TOKEN = "Authorization"
const val INVENTORY_KEY = "inventories"
const val TIME_KEY = "estimate_time"
const val MUSCLE_KEY = "muscle-groups"

const val PERMISSIONS_REQUEST_EX_STOR = 6657;

const val USER_STATS_DATES_PAGE_SIZE = 30;
const val USER_STATS_PAGE_SIZE = 30;

// TODO: https://stackoverflow.com/questions/28373610/android-parse-string-to-date-unknown-pattern-character-x
const val STATISTIC_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ"

