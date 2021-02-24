package ru.workout24.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.workout24.network.User
import io.reactivex.Single


@Dao
interface DaoUser {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: User)


    @Query("""SELECT * from User """)
    fun get(): Single<User>
}