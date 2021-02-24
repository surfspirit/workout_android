package ru.workout24.ui.lk_layer.edit_profile.repository

import android.content.Context
import ru.workout24.network.Api
import ru.workout24.network.BaseResponse
import ru.workout24.network.User
import ru.workout24.room.AppDatabase
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(val context: Context, val api: Api, val db: AppDatabase) {
    private val userDataSource by lazy {
        object : AbstractDataSource<User, User>(context) {
            override fun saveCallResult(request: User) {
                db.daoUser.insert(request)
            }

            override fun loadFromDb(): Flowable<User> {
                return db.daoUser.get().toFlowable()
            }

            override fun createCall(): Flowable<BaseResponse<User>> {
                return api.getAccount()
            }
        }
    }

    fun getUser() = userDataSource
}