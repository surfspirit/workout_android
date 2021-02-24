package ru.workout24.network

import io.reactivex.Completable
import ru.workout24.BuildConfig
import ru.workout24.push.data.DeviceIdDto
import ru.workout24.ui.helpful_info.articles_list.data.model.ArticleCategoriesResponseViewModel
import ru.workout24.ui.statistics_layer.edit_statistics_entry.data.dto.StatisticsEntryResponseDto
import ru.workout24.ui.statistics_layer.statistics_select_trainings.data.dto.FilterDto
import ru.workout24.ui.auth_layer.test_layer.pojos.Exercise
import ru.workout24.ui.trainings.once_exercise.OnceExercise
import ru.workout24.ui.trainings.once_trainings.pojos.OnceTraining
import ru.workout24.ui.trainings.once_trainings.pojos.OnceTrainingFilter
import ru.workout24.ui.trainings.training_programs.pojos.Training
import ru.workout24.ui.trainings.training_programs.pojos.TrainingProgram
import ru.workout24.ui.trainings.training_programs.pojos.TrainingProgramFilter
import ru.workout24.ui.trainings.training_programs.pojos.TrainingProgramShortDescription
import ru.workout24.ui.wg_bonuses.data.dto.WgLeadersDto
import ru.workout24.ui.wg_bonuses_detail.data.dto.WgLeaderDetailDto
import ru.workout24.ui.workout_diary.data.dto.CalendarDto
import ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training.data.dto.TrainingExerciseResultDto
import ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training.data.dto.TrainingResultMetaDto
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import ru.workout24.ui.statistics_layer.statistics.dto.UserStatsDatesDto
import ru.workout24.ui.statistics_layer.statistics.dto.UserStatsPageDto
import ru.workout24.ui.auth_layer.test_layer.pojos.TestExerciseResultDto
import ru.workout24.ui.exercises_execution.EndDto
import ru.workout24.ui.exercises_execution.data.dto.ExerciseResultDto

interface Api {

    /**
     * Получение тестовых упражнений
     */
    @GET("${BuildConfig.API}/test_exercises/")
    fun getExercises(): Flowable<Response<BaseResponse<List<Exercise?>>>>

    /*
    * Сообщить результат прохождения тестового упражнения
    * */
    @POST("${BuildConfig.API}/test_exercises/results")
    fun postTestExerciseResult(@Body body: ExerciseResultDto.TestExerciseDto): Single<BaseResponse<TestExerciseResultDto>>

    /**
     * Получение пользователя
     */
    @GET("${BuildConfig.API}/user")
    fun getUser(): Flowable<Response<User>>

    @GET("${BuildConfig.API}/user")
    fun getUserSingle(): Single<BaseResponse<User>>

    /**
     * Создание аккаунта
     */
    @POST("${BuildConfig.API}/user")
    fun createAccount(
        @Body createUserBody: CreateUserBody
    ): Flowable<Response<BaseResponse<MessageResponse>>>

    /**
     * Получение пользователя
     */
    @GET("${BuildConfig.API}/user")
    fun getAccount(): Flowable<BaseResponse<User>>

    @JvmSuppressWildcards
    @PATCH("${BuildConfig.API}/user")
    fun patchAccount(
        @Body patchFields: Map<String, Any>
    ): Flowable<BaseResponse<StatisticsEntryResponseDto>>

    /**
     * Создание токена
     */
    @POST("${BuildConfig.API}/auth")
    fun createToken(
        @Body userBody: CreateTokenBody,
        @Query("remove_all_tokens") removeAllTokens: Boolean = false
    ): Call<BaseResponse<TokenResponse>>

    /**
     * Создание токена при авторизации через Facebook
     */
    @POST("${BuildConfig.API}/auth/social/fb")
    fun createTokenFacebook(
        @Body tokenBody: CreatePushTokenBody
    ): Single<BaseResponse<TokenResponse>>

    /**
     * Создание токена при авторизации через Google
     */
    @POST("${BuildConfig.API}/auth/social/google")
    fun createTokenGoogle(
        @Body tokenBody: CreatePushTokenBody
    ): Single<BaseResponse<TokenResponse>>

    /**
     * Создание токена при авторизации через Facebook
     */
    @POST("${BuildConfig.API}/auth/social/vk")
    fun createTokenVk(
        @Body tokenBody: CreatePushTokenBody
    ): Single<BaseResponse<TokenResponse>>

    /**
     * Получение access token для Google
     */
    @POST
    fun getAccessTokenGoogle(
        @Body tokenBody: GoogleAccessTokenBody,
        @Url url: String
    ): Single<GoogleAccessTokenResponse>


    /*
         * Отправка ответов анкеты
         */
    @PATCH("${BuildConfig.API}/user")
    fun sendUserInfo(
        @Body userInfo: HashMap<String, Any>
    ): Flowable<Response<BaseResponse<User>>>

    /**
     * Восстановление пароля
     */
    @POST("${BuildConfig.API}/user/restore_password")
    fun restorePassword(
        @Body userInfo: Any
    ): Flowable<Response<BaseResponse<User>>>

    /**
     * Получение короткого описания программ тренировок
     */
    @GET("${BuildConfig.API}/training_sets/")
    fun getTrainingProgramShortDescription(
        @QueryMap filters: Map<String, String> = hashMapOf()
    ): Single<BaseResponse<List<TrainingProgramShortDescription>>>


    /**
     * Получение фильтрованного короткого описания программ тренировок
     */
    @GET("${BuildConfig.API}/training_sets/")
    fun getTrainingProgramShortDescriptionFiltered(
        @Query("filter_id") filter_id: String,
        @Query("gender") gender: String
    ): Call<BaseResponse<List<TrainingProgramShortDescription>>>


    /**
     * Получение фильтров программ тренировок
     */
    @GET("${BuildConfig.API}/training_sets/filters")
    fun getTrainingProgramFilters(): Flowable<Response<BaseResponse<List<TrainingProgramFilter>>>>


    /**
     * Получение разовых тренировок
     */
    @GET("${BuildConfig.API}/trainings")
    fun getOnceTrainings(): Flowable<Response<BaseResponse<List<OnceTraining>>>>

    /**
     * Получение фильтрованного списка разовых тренировок
     */
    @GET("${BuildConfig.API}/trainings")
    fun getOnceTrainingsFiltered(
        @QueryMap filters: Map<String, String> = hashMapOf()
    ): Single<BaseResponse<List<OnceTraining>>>

    /**
     * Получение одиночной тренировки по идентификатору
     */
    @GET("${BuildConfig.API}/trainings/{id}")
    fun getOnceTrainingById(
        @Path("id") id: String
    ): Single<BaseResponse<Training>>

    /**
     * Получение одиночной тренировки по идентификатору
     */
    @GET("${BuildConfig.API}/trainings/exercise_result")
    fun getOnceTrainingResultById(
        @Query("assignment_id") assignmentId: String
    ): Single<BaseResponse<List<TrainingExerciseResultDto>>>

    /**
     * Получение тренировки по идентификатору
     */
    //TODO:убрать метод
    @GET("${BuildConfig.API}/trainings/{id}")
    fun getTrainingById(
        @Path("id") id: String?
    ): Flowable<Response<BaseResponse<Training>>>


    /**
     * Получение фильтров разовых тренировок
     */
    @GET("${BuildConfig.API}/trainings/filters")
    fun getOnceTrainingFilters(): Flowable<Response<BaseResponse<List<OnceTrainingFilter>>>>

    /**
     * Получение фильтров для тренировок
     */
    @GET("${BuildConfig.API}/trainings/filters")
    fun getTrainingsFilters(): Single<BaseResponse<List<FilterDto>>>

    /*
    * Начать выполнение тренировки
    * */
    @POST("${BuildConfig.API}/trainings/start")
    fun startTraining(@Query("training_id") training_id: String?): Call<BaseResponse<StartTrainingResponse>>

    @POST("${BuildConfig.API}/trainings/start")
    fun startTrainingSingle(@Query("training_id") training_id: String?): Single<BaseResponse<StartTrainingResponse>>

    /*
    * Прервать выполнение тренировки
    * */
    @POST("${BuildConfig.API}/trainings/cancel")
    fun cancelTraining(@Query("assignment_id") assignment_id: String?): Call<BaseResponse<MessageResponse>>

    @POST("${BuildConfig.API}/trainings/cancel")
    fun cancelTrainingSingle(@Query("assignment_id") assignment_id: String): Single<BaseResponse<MessageResponse>>

    /*
    * Сообщить результат прохождения упражнения
    * */
    @POST("${BuildConfig.API}/trainings/exercise_result")
    fun postTrainingExerciseResult(@Query("assignment_id") assignment_id: String?, @Body body: ExerciseResultDto.TrainingExerciseDto): Single<EndDto.TrainingEndDto>

    /*
   * Сообщить результат прохождения разовых упражнения
   * */
    @POST("${BuildConfig.API}/single_exercises/results")
    fun postSingleExResult(@Body body: ExerciseResultDto.SingleExerciseDto): Single<BaseResponse<EndDto.SingleExerciseEndDto>>

    /*
   * Указать результат пройденной тренировки
   * */
    @POST("${BuildConfig.API}/trainings/end")
    fun postTrainingResult(@Query("assignment_id") assignment_id: String?, @Body body: EndTrainingBody): Single<BaseResponse<EndDto.TrainingEndDto>>

    /*
  * Начать выполнение программы тренировок
  * */
    @POST("${BuildConfig.API}/training_sets/start")
    fun startTrainingSet(@Query("training_set_id") training_set_id: String?): Call<BaseResponse<MessageResponse>>

    /*
   * Окончить выполнение программы тренировок
   * */
    @POST("${BuildConfig.API}/training_sets/end")
    fun endTrainingSet(@Query("training_set_id") training_set_id: String?): Call<BaseResponse<MessageResponse>>

    /*
   * Окончить выполнение программы тренировок
   * */
    @POST("${BuildConfig.API}/training_sets/end")
    fun endTrainingProgram(@Query("training_set_id") training_set_id: String): Single<BaseResponse<MessageResponse>>

    /*
    * Загрузить файл
    * */
    @Multipart
    @POST("${BuildConfig.API}/upload/")
    fun postFile(@Part file: MultipartBody.Part): Single<BaseResponse<UrlResponse>>

    /**
     * Получение программы тренировок по ID
     */
    @GET("${BuildConfig.API}/training_sets/{id}")
    fun getTrainingProgramById(@Path("id") id: String?): Flowable<Response<BaseResponse<TrainingProgram>>>

    /**
     * Получение одиночных упражнений
     */
    //TODO:убрать этот метод
    @GET("${BuildConfig.API}/single_exercises")
    fun getOnceExercises(
        @Query("inventories") inventories: String?,
        @Query("muscle_groups") muscle_groups: String?,
        @Query("estimate_time") estimate_time: String?
    ): Flowable<Response<BaseResponse<List<OnceExercise>>>>

    /**
     * Получение одиночных упражнений
     */
    @GET("${BuildConfig.API}/single_exercises/")
    fun getOnceExercises(
        @QueryMap filters: Map<String, String> = hashMapOf()
    ): Single<BaseResponse<List<OnceExercise>>>

    /**
     * Получение фильтров для одиночных упражнений
     */
    @GET("${BuildConfig.API}/single_exercises/filters")
    fun getOnceExercisesFilters(): Single<BaseResponse<List<FilterDto>>>

    /**
     * Получение списка инвентаря
     */
    @GET("${BuildConfig.API}/dictionary/inventories")
    fun getInventories(): Flowable<Response<BaseResponse<InventoryResponse>>>

    /**
     * Получение списка групп мышц
     */
    @GET("${BuildConfig.API}/dictionary/muscle-groups")
    fun getMuscleGroups(): Flowable<Response<BaseResponse<MuscleGroupResponse>>>

    /**
     * Получение списка инвентаря
     */
    @GET("${BuildConfig.API}/dictionary/inventories")
    fun getInventoriesSingle(): Single<BaseResponse<InventoryResponse>>

    /**
     * Получение списка групп мышц
     */
    @GET("${BuildConfig.API}/dictionary/muscle-groups")
    fun getMuscleGroupsSingle(): Single<BaseResponse<MuscleGroupResponse>>

    /**
     * Сдвинуть тренировку в конец программы
     */
    @POST("${BuildConfig.API}/training_sets/move")
    fun moveTrainingToTrainingSetEnd(
        @Query("assignment_id") assignment_id: String,
        @Query("training_set_id") training_set_id: String
    ): Single<BaseResponse<Any>>

    /*
     * Добавить запись статистики
     */
    @JvmSuppressWildcards
    @POST("${BuildConfig.API}/user_stats")
    fun addStatisticsEntrySingle(
        @Body patchFields: Map<String, Any>
    ): Single<BaseResponse<StatisticsEntryResponseDto>>

    @JvmSuppressWildcards
    @POST("${BuildConfig.API}/user_stats")
    fun addStatisticsEntry(
        @Body patchFields: Map<String, Any>
    ): Flowable<BaseResponse<StatisticsEntryResponseDto>>

    @JvmSuppressWildcards
    @PATCH("${BuildConfig.API}/user_stats/{id}")
    fun patchStatisticsEntry(
        @Body patchFields: Map<String, Any>,
        @Path("id") entryId: String
    ): Flowable<BaseResponse<StatisticsEntryResponseDto>>

    @DELETE("${BuildConfig.API}/user_stats/{id}")
    fun deleteStatisticsEntry(
        @Path("id") entryId: String
    ): Flowable<BaseResponse<Any>>

    /**
     * Загрузить файл
     */
    @Multipart
    @POST("${BuildConfig.API}/upload")
    fun uploadFile(
        @Part file: MultipartBody.Part, @Part("file") name: RequestBody
    ): Flowable<BaseResponse<UploadedFile>>

    /**
     * Получение списка записей статистики
     */
    @GET("${BuildConfig.API}/user_stats")
    fun getUserStats(
        @Query("page") page: Int?,
        @Query("limit") limit: Int?,
        @Query("day") day: String?
    ): Single<BaseResponse<UserStatsPageDto>>

    /*
     * Получение списка дат, где есть статистика
     */
    @GET("${BuildConfig.API}/user_stats/dates")
    fun getUserStatsDates(
        @Query("page") page: Int?,
        @Query("limit") limit: Int?
    ): Single<BaseResponse<UserStatsDatesDto>>


    /**
     * Получение календаря тренировок
     */
    @GET("${BuildConfig.API}/calendar/{year}/{month}")
    fun getCalendar(
        @Path("year") year: Int,
        @Path("month") month: Int
    ): Single<BaseResponse<CalendarDto>>


    /*
     * Получение всех категорий
     */
    @GET("${BuildConfig.API}/articles")
    fun getArticleCategories(): Single<BaseResponse<ArticleCategoriesResponseViewModel>>

    /*
     * Смена пароля
     */
    @POST("${BuildConfig.API}/user/change_password_current_user")
    fun postChangePassword(@Body body: ChangePasswordBody): Single<BaseResponse<MessageResponse>>

    /**
     * Получить доп. информацию о результатах тренировки
     */

    @GET("${BuildConfig.API}/trainings/result")
    fun getTrainingMetaResult(
        @Query("assignment_id") assignment_id: String
    ): Single<BaseResponse<TrainingResultMetaDto>>

    /**
     * Получение календаря тренировок
     */
    @GET("${BuildConfig.API}/wg/leaderboard")
    fun getWgLeaders(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<BaseResponse<WgLeadersDto>>

    /**
     * Получение календаря тренировок
     */
    @GET("${BuildConfig.API}/wg/{id}")
    fun getWgLeaderDetail(
        @Path("id") id: String
    ): Single<BaseResponse<WgLeaderDetailDto>>

    @POST("${BuildConfig.API}/user/device")
    fun registerDevice(
        @Body deviceId: DeviceIdDto
    ): Single<BaseResponse<Any>>

    @DELETE("${BuildConfig.API}/user/device/{id}")
    fun removeDevice(
        @Path("id") deviceId: String
    ): Completable

    @GET("https://api.vk.com/method/users.get")
    fun getVkProfile(
        @Query("user_ids") userId: Int,
        @Query("access_token") accessToken: String,
        @Query("v") version: String = "5.103",
        @Query("fields") fields: ArrayList<String> = arrayListOf(
            "bdate",
            "photo_max",
            "sex",
            "timezone"
        )
    ): Single<Any>

    @POST("${BuildConfig.API}/debug/set_subscription")
    fun subscribeDebug(
        @Body s: SubscribeBody
    ): Completable

    @POST("${BuildConfig.API}/user/social_link/{social}")
    fun sendSocial(
        @Path("social") social: String,
        @Body token: CreatePushTokenBody
    ): Single<BaseResponse<Any>>

    @DELETE("${BuildConfig.API}/user/social_link/{social}")
    fun deleteSocial(
        @Path("social") social: String
    ): Single<BaseResponse<Any>>

    @PATCH("${BuildConfig.API}/user/timezone")
    fun changeTimezone(
        @Body timezoneBody: TimezoneBody
    ): Single<BaseResponse<Any>>

    @POST("${BuildConfig.API}/debug/harakiri")
    fun deleteAccount(): Completable
}