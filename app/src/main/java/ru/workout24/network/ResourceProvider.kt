package ru.workout24.network

import android.util.Log
import ru.workout24.room.AppDatabase
import ru.workout24.ui.helpful_info.articles_list.data.model.ArticleCategoriesResponseViewModel
import ru.workout24.ui.helpful_info.articles_list.data.model.ArticleCategoryViewModel
import ru.workout24.ui.statistics_layer.statistics_select_trainings.data.dto.FilterDto
import ru.workout24.ui.trainings.once_exercise.OnceExercise
import ru.workout24.ui.trainings.once_trainings.pojos.OnceTraining
import ru.workout24.ui.trainings.training_programs.pojos.Training
import ru.workout24.ui.wg_bonuses_detail.data.dto.WgLeaderDetailDto
import ru.workout24.ui.workout_diary.data.dto.CalendarDto
import ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training.data.dto.TrainingExerciseResultDto
import ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training.data.dto.TrainingResultMetaDto
import ru.workout24.utills.NetworkResource
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import ru.workout24.ui.statistics_layer.edit_statistics_entry.data.dto.StatisticsEntryResponseDto
import ru.workout24.ui.statistics_layer.statistics.dto.*
import ru.workout24.ui.trainings.training_programs.pojos.TrainingProgramShortDescription
import ru.workout24.utills.GENDER
import ru.workout24.utills.GOAL
import ru.workout24.utills.STATISTIC_DATE_PATTERN
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * Класс который обеспечиывает создание ресурсов с их логикой выполнения запросов, обработки данных, сохранения в кэш
 */
class ResourceProvider(
    val api: Api,
    val db: AppDatabase
) {
    val userResource by lazy {
        object : NetworkResource.WithoutQueryWithError<User, User>() {
            override fun getRetrofitCall(): Single<BaseResponse<User>> = api.getUserSingle()

            override fun getFromDb(): Single<User> = db.daoUser.get()

            override fun saveCallResult(response: User) = db.daoUser.insert(response)
        }
    }

    class UserStatisticsResource(val api: Api, val db: AppDatabase) :
        NetworkResource.WithQueryWithError<UserStatsPageDto, List<UserStatistic>, UserStatsQueryDto>() {
        var hasMore = true
        override fun getRetrofitCall(): Single<BaseResponse<UserStatsPageDto>> = query?.let {
            api.getUserStats(it.page, it.limit, it.day)
        } ?: api.getUserStats(null, null, null)

        override fun getFromDb(): Single<List<UserStatistic>> = query?.let {
            db.daoUserStatistics.getAll(it.day)
        } ?: db.daoUserStatistics.getAll("")

        override fun saveCallResult(response: UserStatsPageDto) {
            hasMore = response.hasMore
            val parser = SimpleDateFormat(STATISTIC_DATE_PATTERN, Locale.getDefault())
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            for (i in response.data.indices) {
                try {
                    response.data[i].createdDay =
                        formatter.format(parser.parse(response.data[i].createdAt))
                } catch (e: Exception) {
                    Log.e(
                        this::javaClass.name,
                        "Пришел некорректный формат данных $STATISTIC_DATE_PATTERN"
                    )
                }
            }
            db.daoUserStatistics.insertList(response.data)
        }

        override fun clearCacheInNeeded() {
            db.daoUserStatistics.clearAll()
        }
    }

    val userStatisticsResource
        get() = UserStatisticsResource(api, db)

    class UserStatisticsDatesResource(val api: Api, val db: AppDatabase) :
        NetworkResource.WithoutDbSavingWithQueryWithError<UserStatsDatesDto, /*List<UserStatsDate>,*/ UserStatsDatesQuery>() {
        var hasMore = true
        override fun getRetrofitCall(): Single<BaseResponse<UserStatsDatesDto>> = query?.let {
            api.getUserStatsDates(it.page, it.limit)
        } ?: api.getUserStatsDates(null, null)

        override fun saveCallResult(response: UserStatsDatesDto) {
            hasMore = response.hasMore
            super.saveCallResult(response)
        }

//        override fun getFromDb(): Single<List<UserStatsDate>> =
//            db.daoUserStatsDates.getAll()

//        override fun saveCallResult(response: UserStatsDatesDto) {
//            hasMore = response.hasMore
//            response.data?.let {
//                val list = arrayListOf<UserStatsDate>()
//                for (item in it)
//                    list.add(UserStatsDate(date = item))
//                db.daoUserStatsDates.insertList(list)
//            }
//        }
//
//        override fun clearCacheInNeeded() {
//            db.daoUserStatsDates.clearAll()
//        }
    }

    val userStatisticsDatesResource
        get() = UserStatisticsDatesResource(api, db)


    /**
     * Ресуср с фильтрами тренировок и упражнений для статистики
     */
    val filtersResource
        get() = object : NetworkResource.WithoutQueryWithError<List<FilterDto>, List<FilterDto>>() {
            override fun getRetrofitCall(): Single<BaseResponse<List<FilterDto>>> = Single.zip(
                api.getTrainingsFilters(),
                api.getOnceExercisesFilters(),
                BiFunction { trainingsFilters, onceExFilters ->
                    val response =
                        BaseResponse<List<FilterDto>>()
                    val data = mutableListOf<FilterDto>()
                    trainingsFilters.data?.let {
                        data.addAll(it)
                    }
                    onceExFilters.data?.let {
                        data.addAll(it)
                    }
                    response.data = data
                    response
                }
            )

            override fun getFromDb(): Single<List<FilterDto>> =
                db.daoFilters.getAll()

            override fun saveCallResult(response: List<FilterDto>) {
                db.daoFilters.insertList(response)
            }
        }

    val inventoryResource
        get() = object :
            NetworkResource.WithoutDbSavingWithQueryWithError<InventoryResponse, Nothing>() {
            override fun getRetrofitCall(): Single<BaseResponse<InventoryResponse>> =
                api.getInventoriesSingle()
        }

    val muscleGroupsResource
        get() = object :
            NetworkResource.WithoutDbSavingWithQueryWithError<MuscleGroupResponse, Nothing>() {
            override fun getRetrofitCall(): Single<BaseResponse<MuscleGroupResponse>> =
                api.getMuscleGroupsSingle()
        }

    val onceExercises
        get() = object :
            NetworkResource.WithoutDbSavingWithQueryWithError<List<OnceExercise>/*, List<OnceExercise>*/, Map<String, String>>() {
            override fun getRetrofitCall(): Single<BaseResponse<List<OnceExercise>>> = query?.let {
                api.getOnceExercises(it)
            } ?: api.getOnceExercises()

//            override fun getFromDb(): Single<List<OnceExercise>> =
//                db.daoOnceExercise.getAllSingle()
//
//            override fun saveCallResult(response: List<OnceExercise>) {
//                db.daoOnceExercise.insertList(response)
//            }
        }

    val trainings
        get() = object :
            NetworkResource.WithoutDbSavingWithQueryWithError<List<OnceTraining>/*, List<OnceTraining>*/, Map<String, String>>() {
            override fun getRetrofitCall(): Single<BaseResponse<List<OnceTraining>>> {
                val userGender = userResource.liveData.value?.gender ?: GENDER.NONE
                val userGoals = userResource.liveData.value?.goals ?: GOAL.NONE
                return query?.let {
                    it as HashMap<String, String>
                    it["gender"] = userGender.toString()
                    it["goals"] = userGoals.toString()
                    api.getOnceTrainingsFiltered(it)
                } ?: api.getOnceTrainingsFiltered(hashMapOf("gender" to userGender.toString(), "goals" to userGoals.toString()))
            }

//            override fun getFromDb(): Single<List<OnceTraining>> =
//                db.daoOnceTrainings.getAllSingle()
//
//            override fun saveCallResult(response: List<OnceTraining>) {
//                db.daoOnceTrainings.insertList(response)
//            }
        }

    val calendar
        get() = object :
            NetworkResource.WithQueryWithError<CalendarDto, CalendarDto, Calendar>() {
            // месяц не должен начинаться с нуля
            private val queryMonth get() = query!!.get(Calendar.MONTH) + 1
            private val queryYear get() = query!!.get(Calendar.YEAR)

            override fun getRetrofitCall(): Single<BaseResponse<CalendarDto>> =
                api.getCalendar(queryYear, queryMonth)

            override fun getFromDb(): Single<CalendarDto> {
                return Single.zip(
                    db.daoWorkoutDiary.getSingleTrainingsByMonthAndYear(queryYear, queryMonth),
                    db.daoWorkoutDiary.getSingleExercisesByMonthAndYear(queryYear, queryMonth),
                    db.daoWorkoutDiary.getTrainingSetsByMonthAndYear(queryYear, queryMonth),
                    Function3 { t1, t2, t3 ->
                        CalendarDto(
                            t2, t1, t3
                        )
                    }
                )
            }

            override fun saveCallResult(response: CalendarDto) {
                db.daoWorkoutDiary.insertSingleTrainings(response.trainings.map {
                    it.apply {
                        year = queryYear
                        month = queryMonth
                    }
                })
                db.daoWorkoutDiary.insertSingleExercises(response.singleExercises.map {
                    it.apply {
                        year = queryYear
                        month = queryMonth
                    }
                })
                db.daoWorkoutDiary.insertTrainingSets(response.trainingSets.map {
                    it.apply {
                        year = queryYear
                        month = queryMonth
                    }
                })
            }

            override fun clearCacheInNeeded() {
                db.daoWorkoutDiary.deleteSingleExercisesByMonthAndYear(queryYear, queryMonth)
                db.daoWorkoutDiary.deleteSingleTrainingsByMonthAndYear(queryYear, queryMonth)
                db.daoWorkoutDiary.deleteTrainingSetsByMonthAndYear(queryYear, queryMonth)
            }
        }

    val onceTraining
        get() = object :
            NetworkResource.WithQueryWithError<Training, Training, String>() {
            override fun getRetrofitCall(): Single<BaseResponse<Training>> =
                api.getOnceTrainingById(query!!)

            override fun getFromDb(): Single<Training> = db.daoTrainings.getByIdSingle(query!!)

            override fun saveCallResult(response: Training) {
                db.daoTrainings.insert(response)
            }
        }

    val onceTrainingResult
        get() = object :
            NetworkResource.WithoutDbSavingWithQueryWithError<List<TrainingExerciseResultDto>, String>() {
            override fun getRetrofitCall(): Single<BaseResponse<List<TrainingExerciseResultDto>>> =
                api.getOnceTrainingResultById(query!!)
        }

    val onceTrainingResultMeta
        get() = object :
            NetworkResource.WithoutDbSavingWithQueryWithError<TrainingResultMetaDto, String>() {
            override fun getRetrofitCall(): Single<BaseResponse<TrainingResultMetaDto>> =
                api.getTrainingMetaResult(query!!)
        }

    val categories
        get() = object :
            NetworkResource.WithoutQueryWithError<ArticleCategoriesResponseViewModel, List<ArticleCategoryViewModel>>() {
            override fun getRetrofitCall(): Single<BaseResponse<ArticleCategoriesResponseViewModel>> =
                api.getArticleCategories()

            override fun getFromDb(): Single<List<ArticleCategoryViewModel>> =
                db.daoCategories.getAll()

            override fun saveCallResult(response: ArticleCategoriesResponseViewModel) {
                db.daoCategories.insertList(response.categories)
            }
        }

    val changePassword
        get() = object :
            NetworkResource.WithoutDbSavingWithQueryWithError<MessageResponse, ChangePasswordBody>() {
            override fun getRetrofitCall(): Single<BaseResponse<MessageResponse>> =
                api.postChangePassword(query!!)
        }

    //    val wgLeaders
//        get() = object :
//            NetworkResource.WithoutDbSavingWithQueryWithError<List<WgLeaderDto>, PaginationQuery>() {
//            override fun getRetrofitCall(): Single<BaseResponse<List<WgLeaderDto>>> =
//                api.getWgLeaders(query!!.page, query!!.limit)
//        }
    val wgLeaderDetail
        get() = object :
            NetworkResource.WithoutDbSavingWithQueryWithError<WgLeaderDetailDto, String>() {
            override fun getRetrofitCall(): Single<BaseResponse<WgLeaderDetailDto>> =
                api.getWgLeaderDetail(query!!)
        }

    val addStatisticsEntry
        get() = object :
            NetworkResource.WithoutDbSavingWithQueryWithError<StatisticsEntryResponseDto, Map<String, Any>>() {
            override fun getRetrofitCall(): Single<BaseResponse<StatisticsEntryResponseDto>> =
                api.addStatisticsEntrySingle(query!!)
        }

    val programShortDescription
        get() = object :
            NetworkResource.WithoutDbSavingWithQueryWithError<List<TrainingProgramShortDescription>, Map<String, String>>() {
            override fun getRetrofitCall(): Single<BaseResponse<List<TrainingProgramShortDescription>>> {
                val userGender = userResource.liveData.value?.gender ?: GENDER.NONE
                return query?.let {
                    it as HashMap<String, String>
                    it["gender"] = userGender.toString()
                    api.getTrainingProgramShortDescription(it)
                } ?: api.getTrainingProgramShortDescription(hashMapOf("gender" to userGender.toString()))
            }

//            override fun getFromDb(): Single<List<TrainingProgramShortDescription>> {
//                return db.daoTrainingProgramsShort.getAll()
//            }
//
//            override fun saveCallResult(response: List<TrainingProgramShortDescription>) {
//                db.daoTrainingProgramsShort.insertList(response)
//            }
        }
}