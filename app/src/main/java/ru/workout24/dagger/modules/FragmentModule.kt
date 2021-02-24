package ru.workout24.dagger.modules

import ru.workout24.dagger.FragmentScope
import ru.workout24.ui.auth_layer.anket_layer.final_part.TrainingGoalFragment
import ru.workout24.ui.auth_layer.anket_layer.final_part.TrainingLevelFragment
import ru.workout24.ui.auth_layer.anket_layer.final_part.WeightHeightFragment
import ru.workout24.ui.auth_layer.apply_notifications.ApplyNotificationsFragment
import ru.workout24.ui.article.ArticleFragment
import ru.workout24.ui.auth_layer.register_login.ForgotPasswordFragment
import ru.workout24.ui.auth_layer.register_login.RegisterLoginFragment
import ru.workout24.ui.auth_layer.register_login.tabs.LoginFragment
import ru.workout24.ui.auth_layer.register_login.tabs.RegisterFragment
import ru.workout24.ui.enter.EnterFragment
import ru.workout24.ui.auth_layer.gender_choice.GenderChoiceFragment
import ru.workout24.ui.helpful_info.articles_list.ArticlesListFragment
import ru.workout24.ui.helpful_info.HelpfulInfoFragment
import ru.workout24.ui.lk_layer.change_password.ChangePasswordFragment
import ru.workout24.ui.workout_diary_planing_done.diary_planing_done_exercise.DiaryPlaningDoneExerciseFragment
import ru.workout24.ui.workout_diary_planing_done.diary_planing_done_exercise.DiaryDoneExercisePlanFragment
import ru.workout24.ui.workout_diary_planing_done.diary_planing_done_exercise.DiaryDoneExerciseResultsFragment
import ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training.*
import ru.workout24.ui.lk_layer.edit_profile.EditProfileFragment
import ru.workout24.ui.wg_bonuses_detail.info_new_opportunities.InfoNewOpportunitiesFragment
import ru.workout24.ui.lk_layer.personal_cab.PersonalCabinetFragment
import ru.workout24.ui.statistics_layer.statistics_select_trainings.StatisticsTrainingsFilterFragment
import ru.workout24.ui.statistics_layer.edit_statistics_entry.EditStatisticsEntryFragment
import ru.workout24.ui.statistics_layer.statistics.StatisticsDateFragment
import ru.workout24.ui.statistics_layer.statistics.StatisticsFragment
import ru.workout24.ui.statistics_layer.statistics_select_trainings.TrainingsOrOnceExercisesFilterFragment
import ru.workout24.ui.sub_layer.subscription.*
import ru.workout24.ui.auth_layer.test_layer.*
import ru.workout24.ui.trainings.TrainingsFragment
import ru.workout24.ui.trainings.once_exercise.OnceExercisesFragment
import ru.workout24.ui.statistics_layer.once_exercises_trainings.SelectOnceExerciseOrTrainingsFragment
import ru.workout24.ui.trainings.once_trainings.OnceTrainingListFragment
import ru.workout24.ui.trainings.pager.OnceExercisesFilterFragment
import ru.workout24.ui.trainings.pager.TrainingCategoriesFragment
import ru.workout24.ui.trainings.training_programs.TrainingProgramsFragment
import ru.workout24.ui.trainings.training_programs.program.TrainingProgramFragment
import ru.workout24.ui.trainings.training_programs.training.AboutTrainingFragment
import ru.workout24.ui.wg_bonuses.WgBonusesFragment
import ru.workout24.ui.wg_bonuses_detail.WgBonusesDetailFragment
import ru.workout24.ui.workout_diary.WorkoutDiaryFragment
import ru.workout24.utills.PlaygroundFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.workout24.ui.exercises_execution.ExercisesExecutionHostFragment
import ru.workout24.ui.trainings.exercise_description.ExerciseDescriptionFragment

@Module
abstract class FragmentModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributSubscriptionStartFragment(): SubscriptionStartFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributStartTestFragment(): StartTestFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributSureNotSubscribeFragment(): SureNotSubscribeFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributBestSubscriptionDecision(): BestSubscriptionDecision

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributExercisesFragment(): ExercisesFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributGenderChoiceFragment(): GenderChoiceFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributSubscriptionPreviewFragment(): SubscriptionPreviewFragment


    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributTestListFragment(): TestListFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributLoginFragment(): LoginFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributRegisterFragment(): RegisterFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributSubscriptionPagerFragment(): SubscriptionPagerFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributChooseSubscriptionFragment(): ChooseSubscriptionFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributGlobalChooseSubscriptionFragment(): GlobalChooseSubscriptionFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributSubscriptionInfoFragment(): SubscriptionInfoFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributApplyNotificationsFragment(): ApplyNotificationsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributForgotPasswordFragment(): ForgotPasswordFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributRegisterLoginFragment(): RegisterLoginFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributEnterFragment(): EnterFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributWeightHeightFragment(): WeightHeightFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributTrainingLevelFragment(): TrainingLevelFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributTrainingGoalFragment(): TrainingGoalFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributTestFinishedFragment(): TestFinishedFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributMenuFragment(): TrainingsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributOnceExercisesFilterFragment(): OnceExercisesFilterFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributTrainingCategoriesFragment(): TrainingCategoriesFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributTrainingProgramsFragment(): TrainingProgramsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributOnceExercisesFragment(): OnceExercisesFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributOnceTrainingListFragment(): OnceTrainingListFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributTrainingProgramFragment(): TrainingProgramFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributAboutTrainingFragment(): AboutTrainingFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributPlaygroundFragment(): PlaygroundFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributPersonalCabinetFragment(): PersonalCabinetFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributEditProfileFragment(): EditProfileFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributExerciseDescriptionFragment(): ExerciseDescriptionFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributChangePasswordFragment(): ChangePasswordFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributStatisticsFragment(): StatisticsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributEditStatisticsEntryFragment(): EditStatisticsEntryFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributStatisticsDateFragment(): StatisticsDateFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributStatisticsTrainingsFilterFragment(): StatisticsTrainingsFilterFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributSelectOnceExerciseFragment(): SelectOnceExerciseOrTrainingsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributTrainingsOrOnceExercisesFilterFragment(): TrainingsOrOnceExercisesFilterFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributWgBonusesFragment(): WgBonusesFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributWgBonusesDetailFragment(): WgBonusesDetailFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributWorkoutDiaryFragment(): WorkoutDiaryFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributDiaryDoneTrainingFragment(): DiaryPlaningDoneTrainingFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributDiaryDoneTrainingPlanFragment(): DiaryDoneTrainingPlanFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributDiaryDoneTrainingResultsFragment(): DiaryDoneTrainingResultsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributDiaryDoneExerciseFragment(): DiaryPlaningDoneExerciseFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributDiaryDoneExercisePlanFragment(): DiaryDoneExercisePlanFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributDiaryDoneExerciseResultsFragment(): DiaryDoneExerciseResultsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributInfoNewOpportunitiesFragment(): InfoNewOpportunitiesFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributHelpfulInfoFragment(): HelpfulInfoFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributArticlesListFragment(): ArticlesListFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributArticleFragment(): ArticleFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributExercisesExecutionHostFragment(): ExercisesExecutionHostFragment
}