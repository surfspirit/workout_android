package ru.workout24.room.dao

import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.SerializedName
import io.reactivex.Flowable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Dao
interface DaoExample {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPosts(list: List<Post?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Post?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComments(list: List<Comment?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Comment?)

    @Query("""SELECT * from Post """)
    fun getAll(): Flowable<List<Post>>

    @Query("""SELECT * from Comment """)
    fun getAllComments(): Flowable<List<Comment>>


    @Query("""SELECT
        Post.id as p_id,
        Post.body as p_body,
        Post.title as p_title,
        Post.userId as p_userId,
        Comment.*
        from Post INNER JOIN Comment ON Comment.postId = Post.id""")
    fun getAllPostsWithComments(): Flowable<List<PostWithComments>>


    @Query("""SELECT * from Post WHERE userId=:id""")
    fun getById(id: String): Flowable<Post>
}

@Parcelize
@Entity
data class Post(
    @SerializedName("body") var body: String?,
    @SerializedName("id") @PrimaryKey var id: Int,
    @SerializedName("title") var title: String?,
    @SerializedName("userId") var userId: Int?
) : Parcelable

@Parcelize
@Entity
data class Comment(
    @SerializedName("body") var body: String?,
    @SerializedName("email") var email: String?,
    @SerializedName("id") @PrimaryKey var id: Int,
    @SerializedName("name") var name: String?,
    @SerializedName("postId") var postId: Int?
): Parcelable

@Parcelize
data class PostWithComments(
    @Embedded
    var post: @RawValue Post?,

    @Relation(parentColumn =  "id", entityColumn = "postId", entity = Comment::class )
    var comments: @RawValue List<Comment>?
) : Parcelable


@Parcelize
data class PostsWithComments(
    @Embedded
    var post: @RawValue List<Post>?,

    @Relation(parentColumn =  "id", entityColumn = "postId", entity = Comment::class )
    var comments: @RawValue List<Comment>?
) : Parcelable
